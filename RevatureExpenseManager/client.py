import subprocess
from pathlib import Path
from employee import employee_login
from rich.console import Console


console = Console()

def main():
    while True:
        console.print("\n[bold]--- Revature Expense Manager ---[/bold]")
        print("1. Employee Portal")
        print("2. Manager Portal")
        print("3. Exit")

        options = {
            "1": employee_login,
            "2": launch_manager_portal,
        }

        choice = input("Select an option: ").strip()
        if choice == "3":
            console.print("[yellow]Goodbye.[/yellow]")
            break
        elif choice in options:
            options[choice]()
        else:
            console.print("[red]Invalid option, please try again.[/red]")

def launch_manager_portal():
    # Directory containing this Python file
    base_dir = Path(__file__).resolve().parent

    # Go up one directory, then into manager-app/target
    jar_path = (
        base_dir.parent
        / "manager-app"
        / "target"
        / "manager-app-1.0-SNAPSHOT.jar"
    )

    try:
        subprocess.run(
            ["java", "-jar", str(jar_path)],
            check=True
        )
    except FileNotFoundError:
        print("Error: Java is not installed or the JAR file could not be found.")
    except subprocess.CalledProcessError as e:
        print(f"Manager portal exited with error: {e}")


if __name__ == "__main__":
    main()