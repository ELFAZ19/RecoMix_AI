import joblib
import pandas as pd
import numpy as np
import os

class MovieRecommenderSystem:
    def __init__(self, model_path='models/recommender_system.joblib'):
        """
        Initialize the recommender system with Scikit-Learn SVD
        """
        if not os.path.exists(model_path):
             raise FileNotFoundError(f"Model file not found at {model_path}. Please run 'python train_model.py'.")

        try:
            saved_data = joblib.load(model_path)
        except Exception as e:
             raise RuntimeError(f"Failed to load model: {e}")

        # Unpack data
        self.user_factors = saved_data['user_factors'] # (n_users, n_components)
        self.item_factors = saved_data['item_factors'] # (n_components, n_movies)
        self.user_index = saved_data['user_index']     # Index -> UserID
        self.movie_index = saved_data['movie_index']   # Index -> MovieID
        self.movies = saved_data['movies_df']
        self.ratings = saved_data['ratings_df']
        
        # Mappings for O(1) lookup
        # UserID -> Matrix Row Index
        self.user_id_to_idx = {uid: i for i, uid in enumerate(self.user_index)}
        # MovieID -> Matrix Col Index
        self.movie_id_to_idx = {mid: i for i, mid in enumerate(self.movie_index)}
        # Matrix Col Index -> MovieID
        self.idx_to_movie_id = {i: mid for i, mid in enumerate(self.movie_index)}
        
        # Metadata lookup
        self.movie_titles = dict(zip(self.movies['MovieID'], self.movies['Title']))

    def recommend(self, user_id, n_recommendations=10):
        """
        Generate recommendations using Dot Product of User/Item factors
        """
        # 1. Check if user exists in the matrix
        if user_id not in self.user_id_to_idx:
            return self._cold_start_recommendations(n_recommendations)
            
        user_idx = self.user_id_to_idx[user_id]
        
        # 2. Get user's latent factors
        user_vector = self.user_factors[user_idx] # shape: (n_components,)
        
        # 3. Predict all ratings: dot product of user vector with all item vectors
        # Result shape: (n_movies,)
        predicted_ratings = np.dot(user_vector, self.item_factors)
        
        # 4. Filter already watched movies
        user_history = self.ratings[self.ratings['UserID'] == user_id]
        watched_movie_ids = set(user_history['MovieID'].values)
        
        candidates = []
        for idx, score in enumerate(predicted_ratings):
            movie_id = self.idx_to_movie_id[idx]
            if movie_id not in watched_movie_ids:
                candidates.append((movie_id, score))
        
        # 5. Sort by score
        candidates.sort(key=lambda x: x[1], reverse=True)
        
        # 6. Format Top N
        recommendations = []
        for mid, score in candidates[:n_recommendations]:
            # Normalize score roughly to 1-5 scale for display if needed, 
            # but usually SVD on 1-5 matrix produces values in that range approx.
            # We can clamp it to 5.0 for UI niceness
            display_score = min(5.0, max(1.0, float(score)))
            
            recommendations.append({
                'movie_id': mid,
                'title': self.movie_titles.get(mid, f"Movie {mid}"),
                'predicted_rating': display_score,
                'confidence': 1.0, # Placeholder
                'raw_score': float(score)
            })
            
        return recommendations

    def _cold_start_recommendations(self, n_recommendations=10):
        """
        Popularity fallback for unknown users
        """
        movie_stats = self.ratings.groupby('MovieID').agg({'Rating': ['mean', 'count']}).reset_index()
        movie_stats.columns = ['MovieID', 'avg_rating', 'rating_count']
        movie_stats['score'] = movie_stats['avg_rating'] * movie_stats['rating_count']
        
        top = movie_stats.sort_values('score', ascending=False).head(n_recommendations)
        
        recs = []
        for _, row in top.iterrows():
            mid = row['MovieID']
            recs.append({
                'movie_id': mid,
                'title': self.movie_titles.get(mid, f"Movie {mid}"),
                'predicted_rating': row['avg_rating'],
                'confidence': 0.5,
                'raw_score': 0
            })
        return recs