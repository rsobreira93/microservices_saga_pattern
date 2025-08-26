import os
import threading

threads = []


def build_application(app):
    threads.append(app)
    print("Building application {}".format(app))
    os.system("cd {} && ./gradlew build -x test".format(app))
    print("Application {} finished building!".format(app))
    threads.remove(app)


def docker_compose_up():
    print("Running containers!")
    os.popen("docker compose up --build -d").read()
    print("Pipeline finished!")


def build_all_applications():
    print("Starting to build applications!")
    threading.Thread(target=build_application,
                     args={"order-service"}).start()
    threading.Thread(target=build_application,
                     args={"orchestrator-service"}).start()
    threading.Thread(target=build_application,
                     args={"product-validation-service"}).start()
    threading.Thread(target=build_application,
                     args={"payment-service"}).start()
    threading.Thread(target=build_application,
                     args={"inventory-service"}).start()


def remove_remaining_containers():
    print("Removing all containers.")
    print("Removing docker-compose containers...")
    exit_code = os.system("docker compose down -v --remove-orphans")

    if exit_code == 0:
        print("Docker-compose containers removed successfully.")
    else:
        print("Error while removing docker-compose containers.")

if __name__ == "__main__":
    print("Pipeline started!")
    build_all_applications()
    while len(threads) > 0:
        pass
    remove_remaining_containers()
    threading.Thread(target=docker_compose_up).start()
