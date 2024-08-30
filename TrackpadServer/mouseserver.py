import socket
from pynput.mouse import Controller, Button


mouse = Controller()

HOST = '0.0.0.0'  
PORT = 12345      

#socket
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  
server_socket.bind((HOST, PORT))
server_socket.listen(1)

print(f"Server listening on {HOST}:{PORT}")

try:
    
    client_socket, client_address = server_socket.accept()
    print(f"Connected to {client_address}")

    
    while True:
        data = client_socket.recv(1024).decode().strip()
        if not data:
            break
        
        
        try:
            if data == "left_click":
                mouse.click(Button.left, 1)
            elif data == "right_click":
                mouse.click(Button.right, 1)
            elif data.startswith("scroll"):
                s, dx, dy = data.split(',')
                mouse.scroll(int(dx), int(dy))
            else:
                m,x_offset, y_offset = data.split(',')
                current_position = mouse.position
                mouse.position = (current_position[0] + int(x_offset), current_position[1]+int( y_offset))
        except ValueError:
            print(f"Invalid data received: {data}")
except KeyboardInterrupt:
    print("Server shutting down...")
finally:
    
    if 'client_socket' in locals():
        client_socket.close()
    server_socket.close()
    print("Sockets closed. Server stopped.")
