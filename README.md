# # GBAPIautoRetrofit

Проверки метода GET для endpoint /categories/{id}  - Get Category

    •	Позитивные тесты, запросы товаров по существующим категориям, параметризованный тест для значений:

        o	Id = 1, 'Food', 

        o	Id = 2, 'Electronic'.
      
      Проверка кода ответа 200, списка возвращаемых продуктов на значение categoryTitle. 
    
    •	Негативные тесты:
    
        o	Проверка кода и текста ошибки при запросе неверным методом (POST вместо GET), возвращается код 405 с текстом ошибки “Method not allowed”
        
        o	Запрос со строковым значением вместо числа в параметре id (id = “test”), возвращается код 400 Bad Request
        
        o	 Запрос с несуществующим значением параметра id (id = 0), возвращается код 404 с текстом ошибки “Unable to find category with id: 0”


Проверки метода POST для endpoint /products – Add Product
    •	Позитивные тесты:
        o	Можно добавить новый продукт для значения категории “Food”, возвращается код ответа 201, ненулевое значение id, остальные поля в теле ответа совпадают с      полями продукта в запросе.
        o	Можно добавить новый продукт для значения категории “Electronic”, возвращается код ответа 201, ненулевое значение id, остальные поля в теле ответа совпадают с полями продукта в запросе.
        o	Можно добавить один и тот же продукт дважды, для каждой итерации возвращается новое ненулевое значение id, остальные поля в теле первого и второго ответа совпадают.
        o	Можно успешно добавить продукт со значением title = null в теле запроса, добавляется продукт с пустым полем title.
        o	Можно успешно добавить продукт со значением price = null в теле запроса, добавляется продукт, у которого цена равна нулю.
    •	Негативные тесты:
        o	Запрос на добавление нового продукта с заданным значением id в теле запроса (id в запросе не равен null), возвращается код 400 с текстом ошибки “Id must be null for new entity”.
        o	Запрос на добавление нового продукта с несуществующим/null  значением categoryTitle в теле запроса (categoryTitle = “Clothes”/ categoryTitle = null), возвращается код 500 с текстом ошибки “Internal server error”.
        o	Запрос POST с неверным значением endpoint (адрес запроса “http://localhost:8189/market/api/v1/produc”), возвращается код 404 Not Found 


Проверки метода DELETE для endpoint /products/{id}
    •	Позитивный тест: можно удалить существующий продукт по id
    •	Негативные тесты: 
        o	при запросе на удаление уже удаленного продукта возвращается код 500 ошибки “Internal Server Error”
        o	при запросе на удаление без указания id возвращается код 405 с ошибкой “Method Not Allowed”


Проверки метода GET для endpoint /products/{id}
    •	Позитивный тест: можно получить информацию о существующем продукте по id
    •	Негативный тест: при запросе на получение информации по несуществующему id продукта возвращается код 404 с текстом ошибки “Unable to find product with id”

Проверки метода PUT для endpoint /products/{id} – Modify Product
    •	Позитивные тесты:
        o	Можно изменить title продукта 
        o Можно изменить price продукта
        o Можно изменить categoryTitle продукта на существующий
        o Можно изменить сразу все поля продукта, id остается прежним
    • Негативные тесты:
        o Попытка запроса с id несуществующего продукта, возвращается код 400 с ошибкой "Product with id: {id} doesn't exist"
        o Попытка изменить categoryTitle продукта на несуществующий, возвращается код 500 с ошибкой "Internal Server Error"
        o Попытка передать в теле запроса product с id = null, возвращается код 400 с ошибкой "Id must be not null"