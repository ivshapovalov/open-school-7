
# aop-logger

Проект представляет из себя пример использования Spring AOP для логирования операций в приложении ([Задача](/docs/task.md))

## Документация

* К проекту продключена возможность логирования с помощью log4j2
* Используя проперти app.logging.enable=true (по умолчанию выключено) можно включить или выключить debug-логирование методов в пакете service.
* Логирование реализовано с помощью Spring AOP используя аннотации @Around, @Before, @After. 
* В лог пишутся входящие параметры и исходящий результат метода
* Входящие параметры могут быть аннотированы аннотацией @MethodArgument для указания имени параметра для его логирования

## Отчет
* Проверить логирование можно с помощью запуска тестов mvn clean test
* В пакете com.example.course7.task1.aspect находятся тестовые классы, которые тестируют логирование
* Проверяется наличие/отсутствие в консоли ожидаемых фраз. Например:
  * DEBUG Enter: com.example.course7.task1.service.OrderServiceImpl.getOrder() ....
  * DEBUG Exit: com.example.course7.task1.service.OrderServiceImpl.getOrder() ...
  * ERROR Exception in com.example.course7.task1.service.OrderServiceImpl.getOrder() ...

