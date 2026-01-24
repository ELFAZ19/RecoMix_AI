import requests
import zipfile
import io
import os
import shutil

def download_and_extract_data():
    url = "https://files.grouplens.org/datasets/movielens/ml-100k.zip"
    target_dir = "data/ml-100k"
    
    # Create target directory if it doesn't exist
    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    print(f"Downloading dataset from {url}...")
    try:
        response = requests.get(url)
        response.raise_for_status()
        
        print("Extracting dataset...")
        with zipfile.ZipFile(io.BytesIO(response.content)) as z:
            # The zip file contains a folder 'ml-100k', we want the contents of that folder
            # to be in data/ml-100k/
            
            # Extract to a temporary directory first
            z.extractall("data/temp")
            
            # Move files from data/temp/ml-100k to data/ml-100k
            source_dir = "data/temp/ml-100k"
            for filename in os.listdir(source_dir):
                shutil.move(os.path.join(source_dir, filename), os.path.join(target_dir, filename))
            
            # Cleanup temp directory
            shutil.rmtree("data/temp")
            
        print("Dataset setup complete!")
        print(f"Files extracted to {target_dir}")
        for f in os.listdir(target_dir):
            print(f" - {f}")

    except Exception as e:
        print(f"Error setting up data: {e}")

if __name__ == "__main__":
    download_and_extract_data()
