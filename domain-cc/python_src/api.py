from fastapi import FastAPI

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World2"}


@app.post("/get_classification")
def get_classification():
    return {"classification_code": 6602, "classification_name": "asthma"}

