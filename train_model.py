import pandas as pd
import numpy as np
import joblib
import os
from sklearn.decomposition import TruncatedSVD
from scipy.sparse import csr_matrix

def train_and_save_model():
    print("Loading data...", flush=True)
    
    # Load ratings
    ratings_cols = ['UserID', 'MovieID', 'Rating', 'Timestamp']
    try:
        ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', names=ratings_cols, encoding='latin-1')
    except Exception as e:
        print(f"Error loading ratings data: {e}")
        return

    # Load movies for metadata
    movies_cols = ['MovieID', 'Title', 'ReleaseDate', 'VideoReleaseDate', 'IMDbURL', 
                   'unknown', 'Action', 'Adventure', 'Animation', 'Children', 'Comedy', 
                   'Crime', 'Documentary', 'Drama', 'Fantasy', 'FilmNoir', 'Horror', 
                   'Musical', 'Mystery', 'Romance', 'SciFi', 'Thriller', 'War', 'Western']
    movies = pd.read_csv('data/ml-100k/u.item', sep='|', names=movies_cols, encoding='latin-1')

    print("Creating User-Item Matrix...", flush=True)
    # Create pivot table: Rows=Users, Cols=Movies, Values=Ratings
    # Fill missing values with 0 for SVD
    user_movie_matrix = ratings.pivot(index='UserID', columns='MovieID', values='Rating').fillna(0)
    
    # Convert to sparse matrix for efficiency
    user_movie_sparse = csr_matrix(user_movie_matrix.values)

    print("Training TruncatedSVD (Matrix Factorization)...", flush=True)
    # n_components=20 is a good starting point for small datasets like ml-100k
    svd = TruncatedSVD(n_components=20, random_state=42)
    user_factors = svd.fit_transform(user_movie_sparse)
    item_factors = svd.components_  # shape: (n_components, n_movies)

    print(f"Explained Variance Ratio: {svd.explained_variance_ratio_.sum():.2f}", flush=True)

    # Prepare artifacts
    # We need mappings to convert between real IDs and matrix indices
    save_data = {
        'user_factors': user_factors,
        'item_factors': item_factors,
        'user_index': user_movie_matrix.index,   # maps matrix row index -> UserID
        'movie_index': user_movie_matrix.columns, # maps matrix col index -> MovieID
        'movies_df': movies,  # Metadata
        'ratings_df': ratings # History
    }

    # Ensure models directory exists
    if not os.path.exists('models'):
        os.makedirs('models')
        
    model_path = 'models/recommender_system.joblib'
    print(f"Saving model to {model_path}...", flush=True)
    
    try:
        joblib.dump(save_data, model_path, compress=3)
        print("Model saved successfully!", flush=True)
    except Exception as e:
        print(f"Error saving model: {e}", flush=True)
        
    print("Training complete! Run streamlit app now.", flush=True)

if __name__ == "__main__":
    train_and_save_model()
