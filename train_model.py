import pandas as pd
import numpy as np
import pickle
import os
from lightfm import LightFM
from lightfm.data import Dataset
from scipy import sparse

def train_and_save_model():
    print("Loading data...")
    # Load ratings
    ratings_cols = ['UserID', 'MovieID', 'Rating', 'Timestamp']
    ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', names=ratings_cols, encoding='latin-1')
    
    # Load movies
    movies_cols = ['MovieID', 'Title', 'ReleaseDate', 'VideoReleaseDate', 'IMDbURL', 
                   'unknown', 'Action', 'Adventure', 'Animation', 'Children', 'Comedy', 
                   'Crime', 'Documentary', 'Drama', 'Fantasy', 'FilmNoir', 'Horror', 
                   'Musical', 'Mystery', 'Romance', 'SciFi', 'Thriller', 'War', 'Western']
    movies = pd.read_csv('data/ml-100k/u.item', sep='|', names=movies_cols, encoding='latin-1')
    
    print("Building interaction matrix...")
    # Create LightFM dataset
    dataset = Dataset()
    dataset.fit(users=ratings['UserID'].unique(), items=movies['MovieID'].unique())

    # Build interactions
    # LightFM expects (user, item) or (user, item, weight)
    # We'll use ratings as weights
    (interactions, weights) = dataset.build_interactions(
        (row['UserID'], row['MovieID'], row['Rating']) 
        for index, row in ratings.iterrows()
    )
    
    print("Training LightFM model...")
    # Train LightFM
    # 'warp' loss is good for ranking (implicit feedback, but works for explicit too as ranking)
    # 'logistic' is better for explicit rating prediction (0-1), but here we want recommendations
    model = LightFM(loss='warp', no_components=30, learning_rate=0.05)
    model.fit(interactions, epochs=20, num_threads=2)
    
    # Save model and artifacts
    save_data = {
        'model': model,
        'dataset': dataset, # Save dataset to retrieve mappings later
        'movies': movies,
        'ratings': ratings
    }
    
    # Ensure models directory exists
    if not os.path.exists('models'):
        os.makedirs('models')
        
    model_path = 'models/movie_recommender_model.pkl'
    print(f"Saving model to {model_path}...")
    with open(model_path, 'wb') as f:
        pickle.dump(save_data, f)
        
    print("Training complete!")

if __name__ == "__main__":
    train_and_save_model()
