import com.santaba.agent.groovyapi.http.HTTP
import groovy.json.JsonSlurper

def hostname = hostProps.get("system.hostname")
def password=hostProps.get("awx.pass")
def username=hostProps.get("awx.user")
def port = hostProps.get("awx.port") ? hostProps.get("awx.port").toInteger() : 80

httpClient = HTTP.open(hostname, port)

headers = [
  "Authorization": "Basic ${"${username}:${password}".bytes.encodeBase64().toString()}",
  "Content-Type": "application/json"
]

if (port == 443) {
  protocol = "https"
} else {
  protocol = "http"
}

url = "${procotol}${if(port==443){return "s"}}://${hostname}/api/v2/job_templates/"
response = httpClient.get(url,headers)
if  (httpClient.getStatusCode() != 200 ) { println(response);return 1; }
objects = new JsonSlurper().parseText(httpClient.getResponseBody())
objects.results.each{job ->
  println("${job.id}##${job.name}##${job.summary_fields.project.name}/${job.playbook}####inventory=${job.summary_fields.inventory.name}")
}
return 0
