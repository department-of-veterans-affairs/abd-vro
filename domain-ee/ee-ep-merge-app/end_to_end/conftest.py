import pytest_asyncio
from src.python_src.api import on_shut_down, on_start_up


@pytest_asyncio.fixture(autouse=True, scope="session")
async def endpoint_lifecycle():
    await on_start_up()
    yield
    await on_shut_down()
