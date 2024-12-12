import os
import glob
import subprocess
from concurrent.futures import ThreadPoolExecutor

def compress_file(raw_file):
    output_file = raw_file.replace("raw/", "output/")
    if raw_file.endswith("abc.repx"):
        output_file = output_file.replace("abc.repx", "abx.repx")
    elif raw_file.endswith("def.repx"):
        output_file = output_file.replace("def.repx", "dex.repx")
    elif raw_file.endswith("ghi.repx"):
        output_file = output_file.replace("ghi.repx", "ghx.repx")

    print(f"{raw_file} --> {output_file}")

    # Call the brotli command
    result = subprocess.run(["brotli", "-q", "11", "-vf", "-o", output_file, raw_file],
                            capture_output=True, text=True)

    return result.stdout

def main():
    # Input dir:
    raw_files = glob.glob("raw/*")

    print("::group::Generate `x.repx` using brotli")
    
    # Using ThreadPoolExecutor to run the compression in parallel
    with ThreadPoolExecutor(max_workers=5) as executor:
        # Map the compress_file function to the raw_files list
        results = list(executor.map(compress_file, raw_files))

    # Print the results from the compressions
    for output in results:
        print(output)

    print("::endgroup::")

if __name__ == "__main__":
    main()
