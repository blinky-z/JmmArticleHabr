# HotSpot JIT Disassembler (hsdis)

Для начала необходимо собрать или скачать библиотеку под названием *[hsdis](https://blogs.oracle.com/javamagazine/post/java-hotspot-hsdis-disassembler)* (HotSpot Disassembler) для нужной ОСи и архитектуры процессора. Данная библиотека предоставляет возможность дизассемблирования JIT-сгенерированного нативного кода. Загрузить готовую либу можно отсюда - [https://chriswhocodes.com/hsdis/](https://chriswhocodes.com/hsdis/).

Затем либу необходимо поместить в директорию `<JDK_HOME>/lib/`. 

Теперь можно запускать дизассемблер. Например, для класса `SynchronizedMemoryBarrierJIT`:
```
javac SynchronizedMemoryBarrierJIT.java
java -server -XX:+UnlockDiagnosticVMOptions -XX:+StressLCM -XX:+StressGCM -XX:+PrintAssembly -classpath . SynchronizedMemoryBarrierJIT > console.txt
```

Для того, чтобы JIT отработал, необходимо определенное количество вызовов интересующей функции, так как JIT компилирует нативный код только для "горячих" участков кода. Именно поэтому в приведенных мною примерах интересующая функция вызывается в цикле.

Если вы запускаете hsdis на x86, то можете переключиться с AT&T на Intel синтаксис с помощью параметра `-XX:PrintAssemblyOptions=intel`.

## Other Resources

- [Developers disassemble! Use Java and hsdis to see it all.](https://blogs.oracle.com/javamagazine/post/java-hotspot-hsdis-disassembler)
- [PrintAssembly output explained!](https://jpbempel.github.io/2015/12/30/printassembly-output-explained.html)
- [OpenJDK Wiki: PrintAssembly](https://wiki.openjdk.org/display/HotSpot/PrintAssembly)

