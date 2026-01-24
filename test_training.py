import sys
print("Script started", flush=True)

import pandas as pd
print("Pandas imported", flush=True)

import numpy as np
print("Numpy imported", flush=True)

from lightfm import LightFM
print("LightFM imported", flush=True)

from lightfm.data import Dataset
print("Dataset imported", flush=True)

print("Loading ratings...", flush=True)
ratings = pd.read_csv('data/ml-100k/u.data', sep='\t', names=['UserID', 'MovieID', 'Rating', 'Timestamp'])
print(f"Loaded {len(ratings)} ratings", flush=True)

print("Creating dataset...", flush=True)
dataset = Dataset()
print("Dataset created", flush=True)

print("Fitting dataset...", flush=True)
dataset.fit(users=ratings['UserID'].unique(), items=ratings['MovieID'].unique())
print("Dataset fitted", flush=True)

print("Building interactions...", flush=True)
interactions, weights = dataset.build_interactions(
    (row.UserID, row.MovieID, row.Rating)
    for row in ratings.itertuples(index=False)
)
print("Interactions built!", flush=True)
print(f"Interaction shape: {interactions.shape}", flush=True)
