from config import SQLALCHEMY_DATABASE_URI
from fastapi.encoders import jsonable_encoder
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

engine = create_engine(SQLALCHEMY_DATABASE_URI, pool_pre_ping=True, echo=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class DataBase:

    def with_connection(func):
        def wrapper(self, *args, **kwargs):
            with SessionLocal() as db:
                return func(self, *args, **kwargs, db=db)

        return wrapper

    @with_connection
    def clear(self, model, db):
        db.query(model).delete()
        db.commit()

    @with_connection
    def query_all(self, model, filter, db):
        return db.query(model).filter(filter).all()

    @with_connection
    def query_first(self, model, filter, db):
        return db.query(model).filter(filter).first()

    @with_connection
    def add(self, obj, db):
        db.add(obj)
        db.commit()

    @with_connection
    def update(self, model, filter, obj, db):
        as_json = jsonable_encoder(dict(obj))
        db.query(model).filter(filter).update(as_json)
        db.commit()


data_base = DataBase()
