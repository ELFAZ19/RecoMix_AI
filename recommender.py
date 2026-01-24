import joblib
import pandas as pd
import numpy as np
import os

class MovieRecommenderSystem:
    def __init__(self, model_path='models/recommender_system.joblib'):
        """
        Initialize the recommender system with Scikit-Surprise SVD loaded via Joblib
        """
        # Load trained model and data
        if not os.path.exists(model_path):
             # Try fallback path for simple pickle if joblib doesn't exist yet, or raise error
             # But we want to enforce joblib now.
             raise FileNotFoundError(f"Model file not found at {model_path}. Please run 'python train_model.py' first.")

        try:
            saved_data = joblib.load(model_path)
        except Exception as e:
             raise RuntimeError(f"Failed to load model from {model_path}: {e}")

        # Validate keys
        required_keys = ['model', 'movies', 'ratings']
        for k in required_keys:
            if k not in saved_data:
                 raise KeyError(f"Model file invalid. Missing key: {k}")

        self.model = saved_data['model']
        self.movies = saved_data['movies']
        self.ratings = saved_data['ratings']
        # Trainset is strictly needed for some mapping checks if we wanted to be 100% robust,
        # but pure cold start logic + model.predict() works fine if we handle unknowns.
        # model.predict() does not throw error for unknown users/items, it defaults to mean.
        
        # Fast lookup for movie titles
        self.movie_titles = dict(zip(self.movies['MovieID'], self.movies['Title']))
        self.all_movie_ids = self.movies['MovieID'].unique()

    def recommend(self, user_id, n_recommendations=10):
        """
        Generate recommendations for a user using SVD model
        """
        # 1. Identify movies user has NOT rated
        # We look at the 'ratings' dataframe we loaded
        user_ratings = self.ratings[self.ratings['UserID'] == user_id]
        
        rated_movie_ids = set(user_ratings['MovieID'].values)
        candidates = [m for m in self.all_movie_ids if m not in rated_movie_ids]
        
        if not candidates:
            return [] 
            
        # 2. Predict scores for all candidates
        # SVD.predict uses internal ID mapping if trained, but the .predict() method
        # accepts RAW (external) ids and handles the conversion internally using the trainset
        # that was bound to it during fit().
        predictions = []
        for mid in candidates:
            # predict(uid, iid) -> Prediction tuple. .est is the estimate.
            est = self.model.predict(uid=user_id, iid=mid).est
            predictions.append((mid, est))
            
        # 3. Sort
        predictions.sort(key=lambda x: x[1], reverse=True)
        
        # 4. Format
        recommendations = []
        for mid, score in predictions[:n_recommendations]:
            recommendations.append({
                'movie_id': mid,
                'title': self.movie_titles.get(mid, f"Movie {mid}"),
                'predicted_rating': score,
                'confidence': 1.0, 
                'raw_score': score
            })
            
        return recommendations