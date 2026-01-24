"""
Quick fix script to add the missing 'dataset' key to the model file.
This reconstructs the dataset object from the existing data.
"""
import pickle
import pandas as pd
from lightfm.data import Dataset

print("Loading existing model file...")
with open('models/movie_recommender_model.pkl', 'rb') as f:
    saved_data = pickle.load(f)

print(f"Current keys: {list(saved_data.keys())}")

if 'dataset' not in saved_data:
    print("Missing 'dataset' key - reconstructing it...")
    
    # Reconstruct the dataset object
    ratings = saved_data['ratings']
    movies = saved_data['movies']
    
    dataset = Dataset()
    dataset.fit(
        users=ratings['UserID'].unique(),
        items=movies['MovieID'].unique()
    )
    
    # Build interactions to finalize the dataset mappings
    interactions, weights = dataset.build_interactions(
        (row.UserID, row.MovieID, row.Rating)
        for row in ratings.itertuples(index=False)
    )
    
    # Add dataset to saved_data
    saved_data['dataset'] = dataset
    
    print("Saving fixed model file...")
    with open('models/movie_recommender_model.pkl', 'wb') as f:
        pickle.dump(saved_data, f)
    
    print("✓ Model file fixed successfully!")
    print(f"New keys: {list(saved_data.keys())}")
else:
    print("Dataset key already exists - no fix needed!")
