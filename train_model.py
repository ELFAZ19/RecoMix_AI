import pandas as pd
import pickle
import os
import sys
from surprise import Dataset, Reader, SVD
from surprise.model_selection import cross_validate

def train_and_save_model():
    print("Loading data...", flush=True)
    
    # Load ratings
    ratings_cols = ['UserID', 'MovieID', 'Rating', 'Timestamp']
    # Using 'on_bad_lines' to skip potentially bad lines if pandas version supports it, 
    # but for older pandas 'error_bad_lines=False' was used. 
    # To be safe and compatible with 1.5.3+, we'll use standard load.
    ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', names=ratings_cols, encoding='latin-1')
    
    # Load movies for metadata
    movies_cols = ['MovieID', 'Title', 'ReleaseDate', 'VideoReleaseDate', 'IMDbURL', 
                   'unknown', 'Action', 'Adventure', 'Animation', 'Children', 'Comedy', 
                   'Crime', 'Documentary', 'Drama', 'Fantasy', 'FilmNoir', 'Horror', 
                   'Musical', 'Mystery', 'Romance', 'SciFi', 'Thriller', 'War', 'Western']
    movies = pd.read_csv('data/ml-100k/u.item', sep='|', names=movies_cols, encoding='latin-1')
    
    print("Preparing data for Surprise...", flush=True)
    # Surprise Reader
    reader = Reader(rating_scale=(1, 5))
    
    # Create Surprise Dataset
    data = Dataset.load_from_df(ratings[['UserID', 'MovieID', 'Rating']], reader)
    
    # Build full trainset
    trainset = data.build_full_trainset()
    
    print("Training SVD model (this might take a moment)...", flush=True)
    # Using SVD (Singular Value Decomposition)
    model = SVD()
    model.fit(trainset)
    
    # Save model and artifacts
    save_data = {
        'model': model,
        'movies': movies,
        'ratings': ratings,
        'trainset': trainset  # Useful for debugging or getting raw stats
    }
    
    # Ensure models directory exists
    if not os.path.exists('models'):
        os.makedirs('models')
        
    model_path = 'models/movie_recommender_model.pkl'
    print(f"Saving model to {model_path}...", flush=True)
    with open(model_path, 'wb') as f:
        pickle.dump(save_data, f)
        
    print("Training complete! You can now run the streamlit app.", flush=True)

if __name__ == "__main__":
    train_and_save_model()
