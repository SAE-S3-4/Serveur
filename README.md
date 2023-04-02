# Prerequis
Le serveur as été structuré en telle sorte qu’il puisse être le plus flexible lors des modifications futures. En effet on pourrait l’utiliser en installant juste le JRE de Java 17. 

## Paramétrage
Pour modifier ses paramétrages utiliser les différents arguments listées et expliquées en lançant l’application avec le paramètre *--help*. 
- *-p* : pour modifier le port du serveur 
- *-maxConns* : pour modifier le numéro de connexions en simultanées au serveur admises 
- *-ai* : pour activer ou désactiver l’interaction d’une IA dans l’onglet conversation. 

Si vous lancez le serveur sans paramètres on as également configuré des paramètres par défaut comme : l’utilisation du port 10013, l’activation de l’IA et 1000 connexions en simultanée. 

## Pour que le serveur soit fonctionnel il vous faudra installer docker. 

Il vous suffit de créer un script bash contenant : 
```bash
#! /bin/bash 

docker stop serversaes4 

docker rm serversaes4 

docker rmi terminal 

docker run -d -v /var/run/docker.sock:/var/run/docker.sock -v /home/opc/llama.cpp:/usr/src/app/llama.cpp -p 10013:10013 --name serversaes4 fredegen/serversaes4:VOTRE ARCHITECTURE PROCESSEUR 
```

Ce script stoppe le serveur s’il existe déjà, supprime le conteneur existant s’il a déjà été créé, supprime l’image « terminal » qui est utilisée pour exécuter les commandes de chaque utilisateur et lance le serveur. La commande qui lance le serveur, le lance en mode « daemon » c’est-à-dire en arrière-plan. On lui donne accès au volume /var/run/docker.sock pour qu’il puisse lancer des conteneurs docker. Le port 10013 de la machine hôte est redirigé vers le port 10013 du conteneur car c’est celui que nous utilisons pour les communications entre le serveur et le client. Il sera nommé serversaes4 et sera créé à partir de l’image fredegen/serversaes4 :latest.  

Cette image est celle de notre serveur, si vous ne l’avez pas téléchargée précédemment, elle sera téléchargée automatiquement. 

Il est nécessaire de connaitre votre architecture processeur pour la remplacer dans le script bash. Il est compatible pour les machines amd64 et arm64. Vous avez donc la possibilité de remplacer le tag par ces deux valeurs. 

## Mise en place de l'IA
L’intelligence artificielle ne sera pas installée et mise en place avec l’image docker puisque cette configuration est trop lourde. 

Si vous souhaitez la mettre en place l’IA vous pouvez suivre le mode d’emploi donnée sur le GitHub du projet officiel : https://github.com/ggerganov/llama.cpp 

 

Et une fois que le projet as été configuré il faudra créer un fichier talk.sh contenant ce simple script bash : 
```bash
#!/bin/bash  

./main -m ./models/7B/ggml-model-q4_0.bin --repeat_penalty 1.0 -p "$1" 2>outPut.txt 
```
Ce script sera utilisé par le serveur pour interagir avec l’IA en lui passant un input et en récupérant l’output. 
