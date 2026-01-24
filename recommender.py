import pickle
import pandas as pd
import numpy as np

class MovieRecommenderSystem:
    def __init__(self, model_path='models/movie_recommender_model.pkl'):
        """
        Initialize the recommender system with LightFM
        """
        # Load trained model and data
        with open(model_path, 'rb') as f:
            saved_data = pickle.load(f)
        
        self.model = saved_data['model']
        self.dataset = saved_data['dataset']
        self.movies = saved_data['movies']
        self.ratings = saved_data['ratings']
        self.movie_titles = dict(zip(self.movies['MovieID'], self.movies['Title']))
        
        # internal mappings
        self.user_id_map, self.user_feature_map, self.item_id_map, self.item_feature_map = self.dataset.mapping()
        
        # Reverse map for item ids (internal index -> external MovieID)
        self.inv_item_id_map = {v: k for k, v in self.item_id_map.items()}

    def recommend(self, user_id, n_recommendations=10):
        """
        Generate recommendations for a user
        """
        # Check if user exists in our mappings
        if user_id not in self.user_id_map:
            return self._cold_start_recommendations(n_recommendations)
        
        internal_user_id = self.user_id_map[user_id]
        
        # Items the user has already rated
        user_rated_mask = self.ratings['UserID'] == user_id
        user_rated_movie_ids = set(self.ratings[user_rated_mask]['MovieID'])
        
        # Predict for all items
        n_users, n_items = self.dataset.interactions_shape()
        # item_ids needs to be a list of all internal item indices [0, 1, ... n_items-1]
        all_internal_item_ids = np.arange(n_items)
        
        scores = self.model.predict(internal_user_id, all_internal_item_ids)
        
        # Combine items with scores
        item_scores = list(zip(all_internal_item_ids, scores))
        
        # Sort by score descending
        item_scores.sort(key=lambda x: x[1], reverse=True)
        
        recommendations = []
        count = 0
        
        for internal_item_id, score in item_scores:
            movie_id = self.inv_item_id_map[internal_item_id]
            
            # Skip if already rated
            if movie_id in user_rated_movie_ids:
                continue
                
            recommendations.append({
                'movie_id': movie_id,
                'title': self.movie_titles.get(movie_id, f"Movie {movie_id}"),
                'predicted_rating': 3.5 + (score * 1.0), # Normalize/fake a 5-star scale from score for UI
                'confidence': 1.0 / (1.0 + np.exp(-score)), # Sigmoid for confidence-like score
                'raw_score': score
            })
            
            count += 1
            if count >= n_recommendations:
                break
                
        return recommendations
    
    def _cold_start_recommendations(self, n_recommendations=10):
        """
        Provide recommendations for new users (cold start)
        """
        # Return most popular movies
        movie_stats = self.ratings.groupby('MovieID').agg({
            'Rating': ['mean', 'count']
        }).reset_index()
        
        movie_stats.columns = ['MovieID', 'avg_rating', 'rating_count']
        
        movie_stats['popularity_score'] = (
            movie_stats['avg_rating'] * 
            np.log1p(movie_stats['rating_count'])
        )
        
        top_movies = movie_stats.sort_values('popularity_score', ascending=False).head(n_recommendations)
        recommendations = []
        
        for _, row in top_movies.iterrows():
            recommendations.append({
                'movie_id': row['MovieID'],
                'title': self.movie_titles.get(row['MovieID'], "Unknown"),
                'predicted_rating': row['avg_rating'],
                'confidence': 0.7,
                'reason': 'Popular among all users'
            })
        
        return recommendations