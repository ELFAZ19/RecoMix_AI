import pandas as pd
import pickle
import os
from surprise import Dataset, Reader, SVD
from surprise.model_selection import train_test_split

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
    
    print("Training SVD model...")
    # Prepare data for Surprise
    reader = Reader(rating_scale=(1, 5))
    data = Dataset.load_from_df(ratings[['UserID', 'MovieID', 'Rating']], reader)
    trainset = data.build_full_trainset()
    
    # Train SVD
    algo = SVD()
    algo.fit(trainset)
    
    # Save model and data
    save_data = {
        'model': algo,
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
