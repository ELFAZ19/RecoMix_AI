import pickle
import pandas as pd
import numpy as np
from collections import Counter

class MovieRecommenderSystem:
    def __init__(self, model_path='models/movie_recommender_model.pkl'):
        """
        Initialize the recommender system
        """
        # Load trained model and data
        with open(model_path, 'rb') as f:
            saved_data = pickle.load(f)
        
        self.algo = saved_data['model']
        self.movies = saved_data['movies']
        self.ratings = saved_data['ratings']
        self.movie_titles = dict(zip(self.movies['MovieID'], self.movies['Title']))
    
    def get_user_history(self, user_id):
        """
        Get user's rating history
        """
        user_ratings = self.ratings[self.ratings['UserID'] == user_id]
        history = user_ratings.merge(self.movies[['MovieID', 'Title']], on='MovieID')
        return history[['Title', 'Rating']].to_dict('records')
    
    def recommend(self, user_id, n_recommendations=10):
        """
        Generate recommendations for a user
        """
        # Check if user exists
        if user_id not in self.ratings['UserID'].unique():
            return self._cold_start_recommendations(n_recommendations)
        
        # Get predictions for all movies
        all_movie_ids = self.movies['MovieID'].unique()
        user_rated = set(self.ratings[self.ratings['UserID'] == user_id]['MovieID'])
        
        predictions = []
        for movie_id in all_movie_ids:
            if movie_id not in user_rated:
                pred = self.algo.predict(user_id, movie_id)
                predictions.append({
                    'movie_id': movie_id,
                    'title': self.movie_titles[movie_id],
                    'predicted_rating': pred.est,
                    'confidence': 1.0 - abs(pred.est - 3.5) / 2.5  # Higher for extreme ratings
                })
        
        # Sort by predicted rating and confidence
        predictions.sort(key=lambda x: (x['predicted_rating'], x['confidence']), reverse=True)
        
        return predictions[:n_recommendations]
    
    def _cold_start_recommendations(self, n_recommendations=10):
        """
        Provide recommendations for new users (cold start)
        """
        # Return most popular movies (based on average rating and number of ratings)
        movie_stats = self.ratings.groupby('MovieID').agg({
            'Rating': ['mean', 'count']
        }).reset_index()
        
        movie_stats.columns = ['MovieID', 'avg_rating', 'rating_count']
        
        # Calculate popularity score
        movie_stats['popularity_score'] = (
            movie_stats['avg_rating'] * 
            np.log1p(movie_stats['rating_count'])
        )
        
        # Get top movies
        top_movies = movie_stats.sort_values('popularity_score', ascending=False).head(n_recommendations)
        recommendations = []
        
        for _, row in top_movies.iterrows():
            recommendations.append({
                'movie_id': row['MovieID'],
                'title': self.movie_titles[row['MovieID']],
                'predicted_rating': row['avg_rating'],
                'confidence': 0.7,  # Lower confidence for cold start
                'reason': 'Popular among all users'
            })
        
        return recommendations
    
    def batch_recommend(self, user_ids, n_recommendations=5):
        """
        Generate recommendations for multiple users
        """
        results = {}
        for user_id in user_ids:
            results[user_id] = self.recommend(user_id, n_recommendations)
        return results