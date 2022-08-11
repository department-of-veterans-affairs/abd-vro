# Claims Processor for Diagnostic Code 7101 (Hypertensive vascular disease)

## Tools
[Cerberus](https://docs.python-cerberus.org/en/stable/index.html)\
[RabbitMQ](https://www.rabbitmq.com/)

### RabbitMQ configuration
The application connects to RabbitMQ with the binding key `7101`. Claims are sent to the `health-assess-exchange` and are routed by VASRD code. 

### Event object validation
Incoming messages are validated by Cerberus to avoid container shutdown from unexpected exceptions.

### VRO version 1.0

MedicationRequest objects are evaluated by keyword matching. If any of the identified keywords appear in the medicationRequest object description, they are collected as evidence.

Blood pressure readings are filtered upstream to be <1 year from the date of claim. 

Evidence under `calculated` in the response object is still under development and is not currently in use. 

### Example response

Response objects will resemble the following:

```
{"evidence": {
    "medications": {
        "description": "Benazepril",
        "authoredOn": "2022-04-01",
        "status": "active"},
    "bp_readings": [
        {
            "diastolic": {
                "code": "8462-4",
                "display": "Diastolic blood pressure",
                "unit": "mm[Hg]",
                "value": 115
            },
            "systolic": {                
                "code": "8480-6",
                "display": "Systolic blood pressure",
                "unit": "mm[Hg]",
                "value": 180
            },
            "date": "2021-11-01",
            "practitioner": "DR. JANE460 DOE922 MD",
            "organization": "LYONS VA MEDICAL CENTER"
        }
    ]
},
"calculated": {
    "predominance_calculation": {"success": False},
    "diastolic_history_calculation": {"success": False},
    },
"status": "COMPLETE"
}
```