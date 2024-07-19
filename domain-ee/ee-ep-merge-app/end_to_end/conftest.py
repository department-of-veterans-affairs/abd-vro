import pytest_asyncio

from src.python_src.api import on_shut_down, start_hoppy, start_job_runner


@pytest_asyncio.fixture(autouse=True, scope='session')
async def endpoint_lifecycle():
    await start_hoppy()
    await start_job_runner()
    yield
    await on_shut_down()
