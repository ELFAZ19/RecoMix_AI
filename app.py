import streamlit as st
import pandas as pd
import pickle
import plotly.express as px
from recommender import MovieRecommenderSystem

# Page configuration
st.set_page_config(
    page_title="RecoMix | Movie Recommender",
    page_icon="🍿",
    layout="wide",
    initial_sidebar_state="expanded"
)

# --- CUSTOM CSS & THEME ---
st.markdown("""
<style>
    /* Global Imports */
    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap');
    
    /* Main Background - Dark Cinematic Gradient */
    .stApp {
        background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);
        font-family: 'Poppins', sans-serif;
        color: #e2e8f0;
    }
    
    /* Headings */
    h1, h2, h3, h4 {
        color: #f8fafc !important;
        font-weight: 600;
        text-shadow: 0 2px 4px rgba(0,0,0,0.5);
    }
    
    /* Header Styling */
    .main-header {
        font-size: 3.5rem;
        background: -webkit-linear-gradient(#c084fc, #818cf8);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        text-align: center;
        margin-bottom: 2.5rem;
        font-weight: 800;
        letter-spacing: -1px;
    }
    
    /* Glassmorphism Cards */
    .glass-card {
        background: rgba(255, 255, 255, 0.05);
        backdrop-filter: blur(10px);
        -webkit-backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 16px;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    
    .glass-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.3);
        border-color: rgba(255, 255, 255, 0.2);
    }
    
    /* Movie Card Specifics */
    .movie-card {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
    }
    
    .movie-title {
        font-size: 1.2rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
        color: #fff;
    }
    
    .movie-stats {
        font-size: 0.9rem;
        color: #94a3b8;
    }
    
    .rating-badge {
        background: rgba(245, 158, 11, 0.2);
        color: #fbbf24;
        padding: 4px 8px;
        border-radius: 6px;
        font-weight: bold;
        font-size: 0.9rem;
        display: inline-block;
        margin-top: 0.5rem;
    }
    
    /* Sidebar Styling */
    section[data-testid="stSidebar"] {
        background-color: rgba(15, 23, 42, 0.8) !important;
        border-right: 1px solid rgba(255, 255, 255, 0.05);
    }
    
    /* Metric Styling */
    div[data-testid="stMetricValue"] {
        color: #818cf8 !important;
        font-weight: 700;
    }
    div[data-testid="stMetricLabel"] {
        color: #94a3b8 !important;
    }
    
    /* Custom Button */
    div.stButton > button {
        background: linear-gradient(90deg, #4f46e5 0%, #7c3aed 100%);
        color: white;
        border: none;
        padding: 0.6rem 1.2rem;
        border-radius: 8px;
        font-weight: 600;
        transition: all 0.2s ease;
        width: 100%;
    }
    div.stButton > button:hover {
        opacity: 0.9;
        transform: scale(1.02);
        box-shadow: 0 4px 12px rgba(124, 58, 237, 0.3);
    }

    /* Scrollbar */
    ::-webkit-scrollbar {
        width: 8px;
        height: 8px;
    }
    ::-webkit-scrollbar-track {
        background: #0f172a; 
    }
    ::-webkit-scrollbar-thumb {
        background: #312e81; 
        border-radius: 4px;
    }
    ::-webkit-scrollbar-thumb:hover {
        background: #4f46e5; 
    }
</style>
""", unsafe_allow_html=True)

# Initialize recommender system
@st.cache_resource
def load_recommender():
    return MovieRecommenderSystem('models/recommender_system.joblib')

# Load data
@st.cache_data
def load_data():
    ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', 
                          names=['UserID', 'MovieID', 'Rating', 'Timestamp'])
    movies = pd.read_csv('data/ml-100k/u.item', sep='|', encoding='latin-1',
                         names=['MovieID', 'Title', 'ReleaseDate', 'VideoReleaseDate', 
                                'IMDbURL', 'unknown', 'Action', 'Adventure', 'Animation',
                                'Children', 'Comedy', 'Crime', 'Documentary', 'Drama',
                                'Fantasy', 'FilmNoir', 'Horror', 'Musical', 'Mystery',
                                'Romance', 'SciFi', 'Thriller', 'War', 'Western'])
    return ratings, movies

def main():
    # Load resources
    recommender = load_recommender()
    ratings, movies = load_data()
    
    # Initialize Session State
    if 'watchlist' not in st.session_state:
        st.session_state.watchlist = []
    
    if 'user_id' not in st.session_state:
        st.session_state.user_id = 1
    
    # --- SIDEBAR ---
    with st.sidebar:
        st.markdown("### 👤 User Control")
        # Use simple session state management for inputs to avoid reset issues
        input_user_id = st.number_input(
            "Select User ID",
            min_value=1,
            max_value=ratings['UserID'].max(),
            value=st.session_state.user_id,
            help="Enter a user ID between 1 and 943"
        )
        
        # Update session state if input changes
        if input_user_id != st.session_state.user_id:
            st.session_state.user_id = input_user_id
            st.experimental_rerun()
            
        st.markdown("---")
        st.markdown("### ⚙️ Preferences")
        n_recommendations = st.slider("Recommendations", 6, 24, 12, step=3)
        min_year = st.slider("Min Release Year", 1920, 1998, 1980)
        
        st.markdown("---")
        if st.button("🎲 Random User"):
            random_user = int(ratings['UserID'].sample(1).iloc[0])
            st.session_state.user_id = random_user
            st.experimental_rerun()
            
        st.markdown("---")
        st.caption("RecoMix v1.1 • Built with Streamlit")

    # --- MAIN CONTENT ---
    user_id = st.session_state.user_id
    
    # Title
    st.markdown('<div class="main-header">RecoMix <span style="font-size:1.5rem; vertical-align:middle; opacity:0.7">Movie Recommendations</span></div>', unsafe_allow_html=True)

    # Layout: Stats Row
    col_u1, col_u2, col_u3 = st.columns(3)
    user_ratings = ratings[ratings['UserID'] == user_id]
    
    with col_u1:
        st.metric("Movies Rated", len(user_ratings))
    with col_u2:
        avg_rating = user_ratings['Rating'].mean() if not user_ratings.empty else 0
        st.metric("Avg Rating", f"{avg_rating:.1f} ⭐")
    with col_u3:
        # Simple genre favorite
        if not user_ratings.empty:
            user_movies = movies[movies['MovieID'].isin(user_ratings['MovieID'])]
            genre_cols = ['Action', 'Comedy', 'Drama', 'Romance', 'SciFi', 'Thriller', 'Horror']
            top_genre = user_movies[genre_cols].sum().idxmax()
            st.metric("Fav Genre", top_genre)
        else:
            st.metric("Fav Genre", "N/A")
            
    st.markdown("---")

    # Layout: Recommendations & History
    tab_recs, tab_profile, tab_explore, tab_watchlist = st.tabs(["🔥 Top Picks", "📜 Watch History", "📊 Insights", "📌 Watchlist"])
    
    with tab_recs:
        st.markdown(f"### Curated for User {user_id}")
        
        # Initialize recommendations in session state if not present
        if 'recommendations' not in st.session_state:
            st.session_state.recommendations = []
            
        if st.button("🚀 Generate Recommendations", type="primary", use_container_width=True):
            with st.spinner("Crunching numbers..."):
                # Store in session state
                st.session_state.recommendations = recommender.recommend(user_id, n_recommendations)
        
        # Display recommendations if they exist
        if st.session_state.recommendations:
            recs = st.session_state.recommendations
            
            # Grid Layout for Cards
            cols = st.columns(3) # 3 cards per row
            for idx, rec in enumerate(recs):
                with cols[idx % 3]: # Cycle through columns
                    st.markdown(f"""
                    <div class="glass-card movie-card">
                        <div>
                            <div class="movie-title">{rec['title']}</div>
                            <div class="movie-stats">
                                Prediction: <span class="rating-badge">{rec['predicted_rating']:.1f}/5</span>
                            </div>
                        </div>
                        <div style="margin-top:1rem; font-size:0.8rem; opacity:0.7;">
                            Confidence: {rec['confidence']*100:.0f}%
                        </div>
                    </div>
                    """, unsafe_allow_html=True)
                    
                    # Add to Watchlist Logic
                    if st.button(f"Add to Watchlist", key=f"add_{rec['movie_id']}"):
                        # Check if already in watchlist
                        if not any(item['movie_id'] == rec['movie_id'] for item in st.session_state.watchlist):
                            st.session_state.watchlist.append(rec)
                            st.success(f"Added '{rec['title']}' to your watchlist! 📝")
                        else:
                            st.info(f"'{rec['title']}' is already in your watchlist.")

    with tab_profile:
         if not user_ratings.empty:
            user_history = user_ratings.merge(movies[['MovieID', 'Title', 'ReleaseDate']], on='MovieID')
            user_history = user_history.sort_values('Rating', ascending=False)
            
            st.dataframe(
                user_history[['Title', 'Rating', 'ReleaseDate']].style.background_gradient(cmap='Purples', subset=['Rating']),
                use_container_width=True,
                height=400
            )
         else:
             st.info("No ratings found for this user.")

    with tab_explore:
        col_e1, col_e2 = st.columns(2)
        with col_e1:
            if not user_ratings.empty:
                st.markdown("#### Rating Distribution")
                fig = px.histogram(user_ratings, x='Rating', nbins=5, 
                                   color_discrete_sequence=['#818cf8'])
                fig.update_layout(paper_bgcolor='rgba(0,0,0,0)', plot_bgcolor='rgba(0,0,0,0)', 
                                  font_color='white', xaxis_title="Stars", yaxis_title="Count")
                st.plotly_chart(fig, use_container_width=True)
        
        with col_e2:
            st.markdown("#### Taste Profile (Genres)")
            if not user_ratings.empty:
                user_movies_genres = movies[movies['MovieID'].isin(user_ratings['MovieID'])][genre_cols].sum()
                fig_g = px.pie(values=user_movies_genres.values, names=user_movies_genres.index,
                               color_discrete_sequence=px.colors.sequential.Purples_r)
                fig_g.update_layout(paper_bgcolor='rgba(0,0,0,0)', font_color='white')
                st.plotly_chart(fig_g, use_container_width=True)
    
    with tab_watchlist:
        st.markdown("### 📌 Your Watchlist")
        if st.session_state.watchlist:
            # Clean grid for watchlist
            w_cols = st.columns(3)
            for idx, item in enumerate(st.session_state.watchlist):
                with w_cols[idx % 3]:
                    st.markdown(f"""
                        <div class="glass-card">
                            <div class="movie-title">{item['title']}</div>
                            <div class="movie-stats">
                                Predicted Rating: <span class="rating-badge">{item['predicted_rating']:.1f}</span>
                            </div>
                        </div>
                    """, unsafe_allow_html=True)
                    if st.button("Removing", key=f"rem_{item['movie_id']}"): # Simplified remove for now
                         st.session_state.watchlist.pop(idx)
                         st.experimental_rerun()
        else:
             st.info("Your watchlist is empty. Go to 'Top Picks' to add movies!")

if __name__ == "__main__":
    main()