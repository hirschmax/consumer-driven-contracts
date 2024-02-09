# Consumer Driven Contracts mit Pact
Dieses Beispielprojekt soll die Verwendung von Pact für Consumer Driven Contracts veranschaulichen. 

## Beispielkontext
Das Beispielprojekt sich aus drei Services zusammen, mit denen eine Bestellung von Produkten durchgeführt werden.

![Workflow](cdc-example.drawio.svg)

Der `order`-Service konsumiert in im Beispiel den `receipts`-Service, um für eine Bestellung eine Rechnung zu erhalten. Der `receipts`-Service bekommt eine Liste der Produkt-IDs sowie einen Rabattcode. Zur Berechnung konsumiert der Service die API des `products`-Services, der alle weiteren Informationen zu den Produkten wie Name und Preis liefert.

Die Services enthalten jeweils Tests, in denen die andere Services, von denen sie abhängen, durch Mocks ersetzt werden. Im "produktiven" Code werden die Services mit Hilfe der Rest-Client-Erweiterung für Quarkus definiert, wie im folgenden Beispiel für den `ReceiptService` im `order`-Projekt:

```java
@Path("/receipts")
@RegisterRestClient(configKey = "receipts-api")
public interface ReceiptService {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Receipt calculateReceipt(ReceiptRequest receiptRequest);
}
```

```properties
# Auszug aus der application.properties
quarkus.rest-client."receipts-api".url=http://localhost:8085/api
%test.quarkus.rest-client."receipts-api".url=http://localhost:8095/api
```

## Pact Consumer
In diesem Abschnitt wird beschrieben, wie mit Hilfe der Pact Consumer-Extension für Quarkus ein Contract erzeugt werden kann. Wir nehmen als Beispiel den Service `order`, der die API des Services `receipts` konsumiert.

### Dependency hinzufügen
Um die Quarkus-Extension für Pact verwenden zu können, muss die Dependency zum POM hinzugefügt werden.
 
```xml
<properties>
    <!-- ... -->
    <quarkus-pact.version>1.1.0</quarkus-pact.version>
</properties>
<!-- ... -->
<dependency>
    <groupId>io.quarkiverse.pact</groupId>
    <artifactId>quarkus-pact-consumer</artifactId>
    <version>${quarkus-pact.version}</version>
    <scope>test</scope>
</dependency>
```

### Contract Test
In der Testklasse `UseCaseGetReceiptForOrderTest` wird ein Mock für den Rest-Client-Service `ReceiptService` erstellt. Für einen Pact Consumer Contract-Test wird dieser Mock im Prinzip nur ersetzt.

Wir starten mit einer neuen Testklasse und fügen die notwendigen Annotationen hinzu.

```java
@ExtendWith(PactConsumerTestExt.class)
@MockServerConfig(port = "8095")
@QuarkusTest
public class UseCaseGetReceiptForOrderContractTest {
    // ...
}
```

#### Pact definieren
Ein Pact kann definiert werden, in dem eine Methode erzeugt wird, die ein `PactDslWithProvider`-Object als Parameter hat und einen Pact zurückgibt. Im Beispiel wird die Pact Specification V4 verwendet und dementsprechend ein `V4Pact` zurückgegeben. Des Weiteren muss die Methode mit `@Pact()` annotiert werden.

Die Definition des Pacts enthält alle relevanten Informationen zur API wie Header, Struktur der Request- und ResponseBodies, HTTP-Methode, Pfad und erwarteter Response-Status.
```java
@Pact(consumer = "order", provider = "receipts")
public V4Pact pactToGetReceiptForOneProduct(PactDslWithProvider builder) {
    Map<String, String> headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

    DslPart requestBody = getRequestBodyForOneProduct();
    DslPart responseBody = getResponseBodyForOneProduct();

    return builder
            .uponReceiving("post request")
            .path("/api/receipts")
            .headers(headers)
            .method(HttpMethod.POST)
            .body(requestBody)
            .willRespondWith()
            .status(Response.Status.OK.getStatusCode())
            .headers(headers)
            .body(responseBody)
            .toPact(V4Pact.class);
    }
```
Die JSON-Struktur der Request- und ResponseBody können mit Hilfe der Pact DSL abgebildet werden. Hierdurch wird definiert, wie sich der Mock jeweils verhalten soll. Dabei lassen sich für Attribute sowohl Typen, als auch feste Werte definieren.

Der RequestBody lässt sich z.B. so abbilden:
```java
private DslPart getRequestBodyForOneProduct() {
    return newJsonBody(body -> body
            .array("productIds", array -> array
                    .stringValue("M1")
            )
            .stringType("discountCode")
    ).build();
}
```
Diese Darstellung entspricht in JSON der folgenden Struktur:
```json
{
    "productIds": ["M1"],
    "discountCode": "ABCDEF"
}
```
Die Liste `productIds` bekommt im Beispiel einen festen Wert (`.stringValue("M1")`), `discountCode` hingegen kann ein beliebiger String sein (`.stringType("discountCode")`). Wenn nur ein Typ festgelegt wird, werden die Variablen beim Ausführen durch Zufallswerte gefüllt.

Der ResponseBody lässt sich wie folgt beschreiben:
```java
private DslPart getResponseBodyForOneProduct() {
    return newJsonBody(body -> body
            .array("products", array -> array
                .object(o -> o
                    .stringValue("id", "M1")
                    .stringType("name")
                    .numberType("price")
                )
            )
            .object("total", o -> o
                    .numberType("value")
            )
    ).build();
    }
```
Dies entspricht dem folgenden JSON-Format
```json
{
    "products": [
        {
            "id": "M1",
            "name": "ABCDEF",
            "price": 1234.1234
        }
    ],
    "total": {
        "value": 5678.5678
    }
}
```
Durch die Definition des Contracts wird ausgesagt, dass für die übergebene Product-ID `M1` erwartet wird, dass in der Antwort unter Products eben diese ID zu finden ist. Alle anderen Werte haben keinen vorgegebenen Wert.

#### Test-Methode schreiben
In der Klasse `UseCaseGetReceiptForOrderTest` existiert bereits ein Integrationstest. Diesen können wir in unsere neue Klasse kopieren und dabei noch die Annotation `@PactTestFor()` hinzufügen. Die Assertions müssen ggf. angepasst werden, da keine Logik berücksichtigt wird und hier im Test - abgesehen von der ID - nur zufällige Werte zurückgegeben werden. Wichtig ist außerdem, dass der Wert für `pactMethod` exakt dem Bezeichner der oben beschrieben Methode mit der `@Pact()`-Annotation entspricht.

```java
@Test
@PactTestFor(
        pactMethod = "pactToGetReceiptForOneProduct",
        providerName = "receipts",
        pactVersion = PactSpecVersion.V4
)
public void placeOrderWithOneItem() {
    OrderRequest orderRequest = new OrderRequest(List.of("M1"), "");
    Receipt response = given()
            .body(orderRequest)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .when()
            .post("/order")
            .then()
            .contentType(ContentType.JSON)
            .extract()
            .response()
            .body()
            .as(Receipt.class);

    assertThat(response.products()).hasSize(1);
}
```

#### Pact-Datei
Standardmäßig wird beim Ausführen der Pact-Tests unter `target/pacts` eine JSON-Datei angelegt, die den oben definierten Pact beschreibt. Der Zielordner lässt sich über die Annotation `@PactDirectory()` konfigurieren.

Beim Ausführen des oben definierten Tests wird die Datei `target/pacts/order-receipts.json` mit folgendem Inhalt erstellt:

<details>
  <summary>order-receipts.json</summary>

```json
{
  "consumer": {
    "name": "order"
  },
  "interactions": [
    {
      "comments": {
        "testname": "com.example.order.usecase.UseCaseGetReceiptForOrderContractTest.placeOrderWithOneItem()",
        "text": [

        ]
      },
      "description": "post request for one product",
      "key": "22b72141",
      "pending": false,
      "request": {
        "body": {
          "content": {
            "discountCode": "string",
            "productIds": [
              "M1"
            ]
          },
          "contentType": "application/json",
          "encoded": false
        },
        "generators": {
          "body": {
            "$.discountCode": {
              "size": 20,
              "type": "RandomString"
            }
          }
        },
        "headers": {
          "Content-Type": [
            "application/json"
          ]
        },
        "matchingRules": {
          "body": {
            "$.discountCode": {
              "combine": "AND",
              "matchers": [
                {
                  "match": "type"
                }
              ]
            }
          }
        },
        "method": "POST",
        "path": "/api/receipts"
      },
      "response": {
        "body": {
          "content": {
            "products": [
              {
                "id": "M1",
                "name": "string",
                "price": 100
              }
            ],
            "total": {
              "value": 100
            }
          },
          "contentType": "application/json",
          "encoded": false
        },
        "generators": {
          "body": {
            "$.products[0].name": {
              "size": 20,
              "type": "RandomString"
            },
            "$.products[0].price": {
              "max": 2147483647,
              "min": 0,
              "type": "RandomInt"
            },
            "$.total.value": {
              "max": 2147483647,
              "min": 0,
              "type": "RandomInt"
            }
          }
        },
        "headers": {
          "Content-Type": [
            "application/json"
          ]
        },
        "matchingRules": {
          "body": {
            "$.products[0].name": {
              "combine": "AND",
              "matchers": [
                {
                  "match": "type"
                }
              ]
            },
            "$.products[0].price": {
              "combine": "AND",
              "matchers": [
                {
                  "match": "number"
                }
              ]
            },
            "$.total.value": {
              "combine": "AND",
              "matchers": [
                {
                  "match": "number"
                }
              ]
            }
          }
        },
        "status": 200
      },
      "transport": "https",
      "type": "Synchronous/HTTP"
    }
  ],
  "metadata": {
    "pact-jvm": {
      "version": "4.5.6"
    },
    "pactSpecification": {
      "version": "4.0"
    }
  },
  "provider": {
    "name": "receipts"
  }
}
```
</details>

Die ist der Vertrag, der vom Consumer definiert wurde. Als nächstes wird der Provider betrachtet, der die API bereitstellt und mit einem Verifikationstest sicherstellen muss, dass der Vertrag eingehalten wird.

## Pact Provider
Der hier betrachtete Pact-Provider ist der Service `receipts`.


### Dependency hinzufügen
Analog zum Consumer muss für den Provider ebenfalls eine Dependency zum POM hinzugefügt werden. 
```xml
<properties>
    <!-- ... -->
    <quarkus-pact.version>1.1.0</quarkus-pact.version>
</properties>
<!-- ... -->
<dependency>
    <groupId>io.quarkiverse.pact</groupId>
    <artifactId>quarkus-pact-provider</artifactId>
    <version>${quarkus-pact.version}</version>
    <scope>test</scope>
</dependency>
```

### Contract einfügen
Auf der Provider-Seite muss der vom Consumer erstellte Contract eingebunden werden. Hierfür gibt es grundsätzlich zwei Wege: Kopieren der JSON-Datei in das Provider-Projekt oder die Verwendung eines Brokers. In diesem Beispiel kopieren wir die Datei manuell in das andere Projekt.
```bash
rm -rf ./receipts/src/test/resources/pacts
mkdir -p ./receipts/src/test/resources/pacts
cp ./order/target/pacts/* ./receipts/src/test/resources/pacts
```
Eine ausführliche Dokumentation zum Pact-Broker findet man auf der Seite [https://docs.pact.io/getting_started/sharing_pacts](https://docs.pact.io/getting_started/sharing_pacts).

### Verifikation
Um zu verifizieren, ob die API des Services `receipts` konform zu dem Contract ist, wird in diesem Projekt ein Verifikationstest geschrieben.

In der `@Provider()`-Annotation muss der Name des Providers - in diesem Fall `receipts` - so angegeben werden, wie im Pact-File. Dadurch werden alle Pacts aus dem Zielverzeichnis eingesammelt, die diesem Provider zugeordnet werden. In `@PactFolder()` wird angegeben, in welchen Verzeichnis unter `src/test/recources` die Pact-Dateien liegen. `@QuarkusTest` wird benötigt, damit Context- und Dependency Injection im Test unterstützt werden.

```java
@Provider("receipts")
@PactFolder("pacts")
@QuarkusTest
class UseCaseCalculateReceiptForOrderContractVerificationTest { /* ... */ }
```

In der `setUp()`-Methode muss über den `PactVerificationContext`-Parameter die Zieladresse gesetzt werden, in diesem Fall `localhost` und der in der `application.properties` gesetzte Wert für den Port `quarkus.http.test-port`, unter dem der Service im Test erreichbar ist. Da dieses Beispielprojekt noch einen weiteren MicroService `products` enthält, wird der dafür angelegte RestClient per QuarkusMock simuliert. 

```java
// ...
@ConfigProperty(name = "quarkus.http.test-port")
int quarkusPort;

@BeforeEach
void setUp(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", quarkusPort));
    installMock();
}

private void installMock() {
    MockProductService productServiceMock = new MockProductService();
    productServiceMock.setProductRepository(Map.of(
            "M1", new Product("M1", "Fries", 5.99)
    ));
    QuarkusMock.installMockForType(productServiceMock, ProductService.class, RestClient.LITERAL);
}
// ...
```
Abschließend wird die Methode `pactVerificationTestTemplate()` mit den Annotationen `@TestTemplate` und `@ExtendWith(PactVerificationInvocationContextProvider.class)` hinzugefügt. Hiermit wird über den übergebenen Context-Parameter die Verifikation der Pacts ausgelöst.
```java
@TestTemplate
@ExtendWith(PactVerificationInvocationContextProvider.class)
void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
}
```
Der Test kann nun ausgeführt werden und sollte ohne Fehler durchlaufen.

### Fehlerhafte API
In diesem Abschnitt soll gezeigt werden, wie Pact dabei unterstützen kann, fehlerhafte Spezifikationen frühzeitig zu erkennen, ohne dass die verschiedenen Services gemeinsam ausgerollt wurden.