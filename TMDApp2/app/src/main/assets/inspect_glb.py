import json
import struct
import sys

def parse_glb(file_path):
    with open(file_path, 'rb') as f:
        magic = f.read(4)
        if magic != b'glTF':
            print("Not a valid GLB file")
            return
        version = struct.unpack('<I', f.read(4))[0]
        length = struct.unpack('<I', f.read(4))[0]
        
        chunk0_length = struct.unpack('<I', f.read(4))[0]
        chunk0_type = f.read(4)
        if chunk0_type != b'JSON':
            print("First chunk is not JSON")
            return
            
        json_data = f.read(chunk0_length).decode('utf-8')
        gltf = json.loads(json_data)
        
        print("Meshes:")
        for i, mesh in enumerate(gltf.get('meshes', [])):
            print(f"  [{i}] {mesh.get('name', 'Unnamed')}")
            
        print("\nNodes:")
        for i, node in enumerate(gltf.get('nodes', [])):
            if 'mesh' in node:
                print(f"  [{i}] {node.get('name', 'Unnamed')} -> uses mesh {node['mesh']}")

if __name__ == "__main__":
    parse_glb(sys.argv[1])
