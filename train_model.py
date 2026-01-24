import pandas as pd
import joblib
import os
import sys
from surprise import Dataset, Reader, SVD
from surprise.model_selection import train_test_split

def train_and_save_model():
    print("Loading data...", flush=True)
    
    # Load ratings
    ratings_cols = ['UserID', 'MovieID', 'Rating', 'Timestamp']
    try:
        ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', names=ratings_cols, encoding='latin-1')
    except Exception as e:
        print(f"Error loading ratings data: {e}. defaulting to data/ml-100k path.")
        # Fallback absolute check or just raise
        raise e
    
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
    
    # Build full trainset for the final model
    print("Building full trainset...", flush=True)
    trainset = data.build_full_trainset()
    
    print("Training SVD model...", flush=True)
    # Using SVD (Singular Value Decomposition)
    model = SVD()
    model.fit(trainset)
    
    # Save model and artifacts using joblib
    # We save 'trainset' because it contains the internal-to-external ID mappings
    save_data = {
        'model': model,
        'movies': movies,
        'ratings': ratings, # accurate for stats
        'trainset': trainset
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
        
    print("Training complete! You can now run the streamlit app.", flush=True)

if __name__ == "__main__":
    train_and_save_model()
