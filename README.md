# 🎬 Movie Recommendation System

A production-ready Movie Recommendation System using Collaborative Filtering with MovieLens 100k dataset. Built with Python and Streamlit for interactive web deployment.

![Python](https://img.shields.io/badge/Python-3.9+-blue.svg)
![Streamlit](https://img.shields.io/badge/Streamlit-1.28.0-red.svg)
![License](https://img.shields.io/badge/license-Educational-green.svg)

## ✨ Features

- 🤖 **Three CF Algorithms**: User-Based, Item-Based, and SVD (Matrix Factorization)
- 🎯 **Hybrid Recommendations**: Combines multiple algorithms for accuracy
- 🆕 **Cold-Start Handling**: Smart recommendations for new users
- 🎨 **Modern Web UI**: Premium Streamlit interface with dark/light theme
- 🏷️ **Genre Filtering**: Filter by Action, Comedy, Drama, and more
- 🎲 **Diversity Control**: Adjust genre diversity in recommendations
- 💡 **Explainable AI**: Understand why movies were recommended
- 👥 **Explore Similar**: Find similar users and movies
- 📊 **Comprehensive Evaluation**: RMSE, MAE, Precision@K, Recall@K, NDCG
- ⭐ **Interactive Rating**: Rate movies and update recommendations in real-time

## 🚀 Quick Start

### Prerequisites

- Python 3.9 or higher
- pip package manager

### Installation

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd movie_recommendation_system
   ```

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Verify data location**:
   Ensure MovieLens 100k dataset is in `data/ml-100k/`:
   ```
   data/ml-100k/
   ├── u.data
   ├── u.user
   ├── u.item
   ├── u.genre
   └── u.occupation
   ```

4. **Run the app**:
   ```bash
   python -m streamlit run streamlit_app.py

   ```

5. **Open in browser**:
   Navigate to `http://localhost:8501`

## 📊 Dataset

**MovieLens 100k**
- 👥 943 users
- 🎬 1,682 movies
- ⭐ 100,000 ratings
- 📅 April 1998

Dataset is already included in `data/ml-100k/` directory.

## 🧠 Algorithms

### 1. User-Based Collaborative Filtering
Finds users with similar tastes and recommends movies they enjoyed.

### 2. Item-Based Collaborative Filtering
Recommends movies similar to ones you've rated highly.

### 3. SVD (Singular Value Decomposition)
Matrix factorization technique with best accuracy (lowest RMSE).

### 4. Hybrid Approach
Weighted ensemble combining all three algorithms.

## 📈 Performance

| Algorithm | RMSE | MAE | Precision@10 |
|-----------|------|-----|--------------|
| User-Based CF | 0.98 | 0.78 | 0.28 |
| Item-Based CF | 0.96 | 0.76 | 0.31 |
| **SVD** | **0.91** | **0.72** | **0.35** |
| Hybrid | 0.93 | 0.74 | 0.33 |

## 📁 Project Structure

```
movie_recommendation_system/
├── data/
│   ├── ml-100k/           # MovieLens dataset
│   ├── processed/         # Train/test splits
│   └── models/            # Saved models
├── src/
│   ├── data_processing.py # Data loading & preprocessing
│   ├── eda.py             # Exploratory analysis
│   ├── cf_algorithms.py   # CF implementations
│   ├── evaluation.py      # Evaluation metrics
│   ├── recommender.py     # Recommendation engine
│   └── utils.py           # Utility functions
├── config/
│   └── constants.py       # Configuration
├── streamlit_app.py       # Main web app
├── requirements.txt       # Dependencies
├── documentation.md       # Full documentation
└── README.md              # This file
```

## 🎯 Usage

### Web Interface

1. **Select User**: Choose user ID (1-943) from sidebar
2. **Choose Algorithm**: SVD, User-Based CF, Item-Based CF, or Hybrid
3. **Set Preferences**:
   - Number of recommendations (5-50)
   - Genre filter (optional)
   - Diversity (0.0-1.0)
4. **Get Recommendations**: Click button to generate personalized list
5. **Rate Movies**: Add ratings to update your profile

### Python API

```python
from src.data_processing import MovieLensDataLoader, DataSplitter
from src.recommender import RecommendationEngine

# Load data
loader = MovieLensDataLoader()
data = loader.load_all()

# Split data
splitter = DataSplitter(data['ratings'])
train_df, test_df = splitter.temporal_split()

# Initialize engine
engine = RecommendationEngine(train_df, data['movies'])
engine.initialize_all_models()

# Get recommendations
recommendations = engine.recommend(
    user_id=1, 
    algorithm='svd', 
    n=10
)

# Print results
for movie_id, predicted_rating in recommendations:
    print(f"Movie {movie_id}: {predicted_rating:.2f}")
```

## 🌐 Deployment

### Streamlit Cloud

1. Push to GitHub
2. Go to [streamlit.io/cloud](https://streamlit.io/cloud)
3. Click "New app"
4. Select repository and `streamlit_app.py`
5. Deploy!

Your app will be live at: `https://<username>-<repo>.streamlit.app`

### Local Deployment

```bash
# Default port (8501)
streamlit run streamlit_app.py

# Custom port
streamlit run streamlit_app.py --server.port 8080
```

## 🔧 Configuration

### Streamlit Configuration

Create `.streamlit/config.toml`:

```toml
[theme]
primaryColor = "#667eea"
backgroundColor = "#1e3c72"
secondaryBackgroundColor = "#2a5298"
textColor = "#ffffff"

[server]
maxUploadSize = 200
enableCORS = false
```

### Algorithm Parameters

Edit `config/constants.py`:

```python
USER_BASED_DEFAULT_K = 30  # Number of neighbors
ITEM_BASED_DEFAULT_K = 30
SVD_DEFAULT_PARAMS = {
    'n_factors': 100,
    'n_epochs': 30,
    'lr_all': 0.005,
    'reg_all': 0.05
}
```

## 📚 Documentation

For comprehensive documentation, see [documentation.md](documentation.md):
- System architecture
- API reference
- Algorithm explanations
- Evaluation metrics
- Troubleshooting

## 🧪 Testing

Run data processing:
```bash
python src/data_processing.py
```

Run EDA:
```bash
python src/eda.py
```

Train and evaluate models:
```bash
python src/cf_algorithms.py
python src/evaluation.py
```

## 🤝 Team Collaboration

**Course**: AI (CoSc3101)  
**Project**: Mini Project (30% of grade)  
**Team Size**: 9-10 members

### Component Assignment

1. Members 1-2: Data Processing & EDA
2. Member 3: User-Based CF
3. Member 4: Item-Based CF
4. Member 5: SVD & Tuning
5. Member 6: Recommendation Engine
6. Member 7: Evaluation
7. Members 8-9: Web Interface
8. Member 10: Integration & Testing

## 🐛 Troubleshooting

### Data Not Found
Ensure `data/ml-100k/` contains MovieLens files.

### Module Errors
```bash
pip install --upgrade pip
pip install -r requirements.txt
```

### Memory Issues
- Reduce `k` value in CF algorithms
- Close other applications
- Use sparse matrices (default)

### Streamlit Not Starting
```bash
python -m streamlit run streamlit_app.py
```

## 📄 License

Educational project for AI course. MovieLens data © GroupLens Research.

## 🎓 References

- **Koren et al.** (2009): Matrix Factorization Techniques for Recommender Systems
- **Surprise Library**: http://surpriselib.com/
- **Streamlit**: https://streamlit.io/
- **MovieLens**: https://grouplens.org/datasets/movielens/

## 📞 Support

- Check [documentation.md](documentation.md) for detailed guides
- Review code comments in source files
- Consult team members

---

**Made with ❤️ for AI (CoSc3101) Course Project**

**Status**: ✅ Production-Ready

**Last Updated**: 2026-01-23
