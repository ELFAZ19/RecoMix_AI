import pickle
import pandas as pd
import numpy as np

class MovieRecommenderSystem:
    def __init__(self, model_path='models/movie_recommender_model.pkl'):
        """
        Initialize the recommender system with Scikit-Surprise SVD
        """
        # Load trained model and data
        try:
            with open(model_path, 'rb') as f:
                saved_data = pickle.load(f)
        except FileNotFoundError:
             raise FileNotFoundError(f"Model file not found at {model_path}. Please run 'python train_model.py' first.")

        # Check required keys
        required_keys = ['model', 'movies', 'ratings']
        missing_keys = [k for k in required_keys if k not in saved_data]
        if missing_keys:
             raise KeyError(f"Model file invalid. Missing keys: {missing_keys}. Please retrain.")

        self.model = saved_data['model']
        self.movies = saved_data['movies']
        self.ratings = saved_data['ratings']
        # self.trainset = saved_data.get('trainset', None) # Optional
        
        # Fast lookup for movie titles
        self.movie_titles = dict(zip(self.movies['MovieID'], self.movies['Title']))
        
        # Get list of all movie IDs in the system
        self.all_movie_ids = self.movies['MovieID'].unique()

    def recommend(self, user_id, n_recommendations=10):
        """
        Generate recommendations for a user using SVD model
        """
        # Check if user has rated anything (for cold start logic)
        user_ratings = self.ratings[self.ratings['UserID'] == user_id]
        
        # If user is completely new (empty interactions), SVD can still predict (using global mean + biases),
        # but pure cold start (popularity) might be better. Let's use SVD if they exist in system or if we want SVD's mean handling.
        # Actually, Surprise SVD handles unknown users/items gracefully by default (returns global mean).
        
        # But generally, if we have 0 ratings for this session user in our static dataset, 
        # checking if they exist in the *training* data is what matters for 'personalized' results.
        # However, for this simple apps, we'll assume we want to predict for everyone.
        
        # 1. Identify movies user has NOT rated
        rated_movie_ids = set(user_ratings['MovieID'].values)
        
        candidates = [m for m in self.all_movie_ids if m not in rated_movie_ids]
        
        if not candidates:
            return [] # User saw everything!
            
        # 2. Predict scores for all candidates
        predictions = []
        for mid in candidates:
            # SVD predict().est returns the estimated rating (1-5)
            est = self.model.predict(uid=user_id, iid=mid).est
            predictions.append((mid, est))
            
        # 3. Sort by estimated rating
        predictions.sort(key=lambda x: x[1], reverse=True)
        
        # 4. Format top N
        recommendations = []
        
        for mid, score in predictions[:n_recommendations]:
            recommendations.append({
                'movie_id': mid,
                'title': self.movie_titles.get(mid, f"Movie {mid}"),
                'predicted_rating': score, # Already in 1-5 scale
                'confidence': 1.0, # SVD doesn't provide probability, just an estimate
                'raw_score': score
            })
            
        return recommendations
    
    def _cold_start_recommendations(self, n_recommendations=10):
        """
        Provide popular movies as fallback
        """
        # Calculate popularity similar to before
        movie_stats = self.ratings.groupby('MovieID').agg({
            'Rating': ['mean', 'count']
        }).reset_index()
        movie_stats.columns = ['MovieID', 'avg_rating', 'rating_count']
        
        # Weighted rating (IMDB formula-like) or just simple score
        # Here we use the previous logic
        movie_stats['popularity_score'] = movie_stats['avg_rating'] * np.log1p(movie_stats['rating_count'])
        
        top_movies = movie_stats.sort_values('popularity_score', ascending=False).head(n_recommendations)
        
        recs = []
        for _, row in top_movies.iterrows():
            recs.append({
                'movie_id': row['MovieID'],
                'title': self.movie_titles.get(row['MovieID']),
                'predicted_rating': row['avg_rating'],
                'confidence': 0.8,
                'reason': 'Popular Choice'
            })
        return recs