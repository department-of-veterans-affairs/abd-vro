import logging
import sys
from contextlib import asynccontextmanager

import uvicorn
from fastapi import FastAPI


@asynccontextmanager
async def lifespan(api: FastAPI):
    await on_start_up()
    yield
    await on_shut_down()


async def on_start_up():
    pass


async def on_shut_down():
    pass


app = FastAPI(
    title="EP Merge Tool",
    description="Merge supplemental claim (EP400) contentions to a pending claim (EP 010/020/110).",
    contact={},
    version="v0.1",
    license={
        "name": "CCO 1.0",
        "url": "https://github.com/department-of-veterans-affairs/abd-vro/blob/master/LICENSE.md"
    },
    servers=[
        {
            "url": "/ep",
            "description": "",
        },
    ],
    lifespan=lifespan
)

logging.basicConfig(
    format="[%(asctime)s] %(levelname)-8s %(message)s",
    level=logging.INFO,
    datefmt="%Y-%m-%d %H:%M:%S",
    stream=sys.stdout,
)


@app.get("/health")
def get_health_status():
    return {"status": "ok"}


if __name__ == "__main__":
    uvicorn.run(app, host="localhost", port=8140)
