import subprocess
import requests

BASE_URL="http://127.0.0.1:8000/ExpenseManager"
user_id=None

def main():
    while True:
        print("\n--- Revature Expense Manager ---")
        print("1. Employee Portal")
        print("2. Manager Portal")
        print("3. Exit")

        options = {
            "1": employee_login,
            "2": launch_manager_portal,
        }

        
        choice=input("Select and option: ").strip()
        if choice=="3":
            print("Goodbye")
            break
        elif choice in options:
            options[choice]()
        else:
            print("Invalid option, please try again.")


def launch_manager_portal():
    try:
        subprocess.run(
            ["java", "-jar", "manager-app/target/manager-app-1.0-SNAPSHOT.jar"],
            check=True
        )
    except FileNotFoundError:
        print("Error: Java is not installed or the manager app could not be found.")
    except subprocess.CalledProcessError as e:
        print(f"Manager portal exited with error: {e}")



def employee_login():
    global user_id #keeping track of current user id 
    print("\n--- Employee Login ---")
    username=input("Username: ")
    password= input("Password: ")

    res = requests.post(f"{BASE_URL}/login/", json={"username": username, "password": password})


    


        
    