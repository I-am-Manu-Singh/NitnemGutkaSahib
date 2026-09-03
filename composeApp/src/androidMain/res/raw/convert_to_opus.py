#!/usr/bin/env python3
import os
import sys
import subprocess

def convert_to_opus(input_path, output_path=None):
    if not os.path.isfile(input_path):
        print(f"Error: File not found: {input_path}")
        return False
        
    if not output_path:
        base, _ = os.path.splitext(input_path)
        output_path = f"{base}.opus"
        
    print(f"Converting '{input_path}' -> '{output_path}' (48kHz Mono @ 16 kbps Opus)...")
    cmd = [
        "ffmpeg",
        "-y",
        "-i", input_path,
        "-c:a", "libopus",
        "-b:a", "16k",
        "-ac", "1",
        "-ar", "48000",
        output_path
    ]
    
    result = subprocess.run(cmd)
    if result.returncode == 0:
        print(f"✓ Successfully converted: {output_path}")
        return True
    else:
        print(f"✗ Failed to convert {input_path}")
        return False

def main():
    if len(sys.argv) < 2:
        print("Usage: python3 convert_to_opus.py <audio_file1> [audio_file2 ...]")
        print("   or: python3 convert_to_opus.py --all-mp3  (converts all .mp3 files in current directory)")
        sys.exit(1)
        
    if sys.argv[1] == "--all-mp3":
        script_dir = os.path.dirname(os.path.abspath(__file__))
        mp3_files = [f for f in os.listdir(script_dir) if f.lower().endswith(".mp3")]
        if not mp3_files:
            print("No .mp3 files found in current directory.")
            return
        for f in mp3_files:
            convert_to_opus(os.path.join(script_dir, f))
    else:
        for arg in sys.argv[1:]:
            convert_to_opus(arg)

if __name__ == "__main__":
    main()
