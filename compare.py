from deepface import DeepFace

def main():
    import sys
    if len(sys.argv) != 3:
        print("ERROR|0|0")
        return
    
    img1_path = sys.argv[1]
    img2_path = sys.argv[2]

    try:
        result = DeepFace.verify(img1_path, img2_path, model_name="ArcFace", enforce_detection=False)
        distance = result["distance"]
        threshold = 0.55
        verified = distance < threshold
        similarity = max(0.0, min(100.0, (1 - distance / threshold) * 100))
        status = "ORANG_SAMA" if verified else "ORANG_BERBEDA"
        print(f"{status}|{distance:.4f}|{similarity:.2f}")
    except Exception as e:
        print("ERROR|0|0")

if __name__ == "__main__":
    main()
