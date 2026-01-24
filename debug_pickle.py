import pickle

# Load and inspect the pickle file
try:
    with open('models/movie_recommender_model.pkl', 'rb') as f:
        saved_data = pickle.load(f)
    
    print("Keys in saved_data:", saved_data.keys())
    print("\nType of each value:")
    for key, value in saved_data.items():
        print(f"  {key}: {type(value)}")
except FileNotFoundError:
    print("Model file not found!")
except Exception as e:
    print(f"Error loading pickle: {e}")
    print(f"Error type: {type(e)}")
