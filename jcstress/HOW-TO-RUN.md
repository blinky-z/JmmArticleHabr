# Java Concurrency Stress (jcstress)

## Сборка jcstress

Склонируйте [jcstress](https://github.com/openjdk/jcstress) к себе:
```
git clone https://github.com/openjdk/jcstress.git ~/jcstress
```

Поместите мои тесты в модуль `tests-custom`:
```
cp -r tests ~/jcstress/tests-custom/src/main/java/org/openjdk/jcstress/tests/jmm_custom
```

Соберите JAR файл с тестами:
```
cd ~/jcstress
mvn clean verify -pl tests-custom -am`
````

Собранный jar-файл будет находиться по пути `tests-custom/target/jcstress.jar`.

### Готовые JAR

Вы можете использовать уже собранные мною jar-ники без необходимости скачивать и собирать jcstress - смотрите директорию `jar/`. JAR были собраны с использованием Amazon Corretto 17.

## Запуск тестов

Запустите тесты из корневой директории jcstress:
```
cd ~/jcstress
java -jar tests-custom/target/jcstress.jar -t ".*JmmReordering.*" -v -f 10`
```

Результаты прогона тестов будут лежать в директории `jcstress/results` в виде HTML. 

### Личные результаты запуска тестов

Мои результаты запуска тестов, часть которых я уже приводил в статье, вы можете посмотреть в директории `results`.

## Other Resources

- [https://github.com/openjdk/jcstress](https://github.com/openjdk/jcstress) - github repo
- [Workshop: Java Concurrency Stress (JCStress)](https://youtu.be/koU38cczBy8) - доклад от Алексея Шипилева
  - [Слайды](https://shipilev.net/talks/hydraconf-June2021-jcstress-workshop.pdf)

