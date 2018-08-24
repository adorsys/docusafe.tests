# Document Tests

## Layer
* layer: **docusafe-rest-server**

    Dieses Layer hat hüllt die Bibliotheksdienste von docusafe in ein rest Layer, so dass sie via REST angesprochen werden können.
    Es gibt zwei Controller. Erster ist der, der wirklich alle Dienste des business-layers anbietet.
    Dort können alle Funktionen getestet werden.
    Die über dem BusinessLayer liegenden Module wie Transactional und Cached-Transactional können nur durch den
    zweiten Controller getestet werden. Hier gibt es nur eine Test-Schnittstelle. Anhand der TestParameter werden dann Tests durchgeführt.
    
  
* layer: **docusafe-rest-client-batch ** 
    
    Mit Curl lassen sich nur Requests absetzen, die ganze Datenblöcke auf einmal verschicken. Der RestClient bietet die Möglichkeit, auch Streambasiert zu verschicken bzw. zu empfangen.
    Hier wird auch das Streaming getestet.
    
* layer: **docusafe-rest-client-gui ** 
    
    Ein angular Frontend, mit dem der Test-Controller angesprochen werden kann.


## Release build

Um ein release zu erstellen, sind folgende Schritte notwendig:

    git checkout develop
    git pull
    git submodule init
    git submodule update
    ./release-scripts/release.sh 0.18.8 0.18.9
    git push --atomic origin master develop --follow-tags

Wenn das Script beim release mit folgendem Fehler terminiert

    ! [rejected]        master -> master (non-fast-forward)
    error: failed to push some refs to 'https://github.com/adorsys/cryptoutils'
    hint: Updates were rejected because the tip of your current branch is behind

, dann liegt das daran, das master erst ausgecheckt werden muss:

    git checkout master
    .release-scripts/release.sh .....
    
    
