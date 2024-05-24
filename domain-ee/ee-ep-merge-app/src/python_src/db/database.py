from config import SQLALCHEMY_DATABASE_URI
from fastapi.encoders import jsonable_encoder
from sqlalchemy import create_engine, desc, inspect
from sqlalchemy.exc import SQLAlchemyError
from sqlalchemy.orm import sessionmaker

engine = create_engine(SQLALCHEMY_DATABASE_URI, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class Database:
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
    def query(self, model, filter, order_by, offset, limit, db):
        total = db.query(model).filter(filter).count()
        results = db.query(model).filter(filter).order_by(desc(order_by)).offset((offset - 1) * limit).limit(limit).all()
        return results, total

    @with_connection
    def query_first(self, model, filter, db):
        return db.query(model).filter(filter).first()

    @with_connection
    def add(self, obj, db):
        db.add(obj)
        db.commit()

    @with_connection
    def update(self, obj, db):
        as_json = jsonable_encoder(dict(obj))
        primary_key = inspect(obj.Meta.orm_model).primary_key[0]
        db.query(obj.Meta.orm_model).filter(primary_key == getattr(obj, primary_key.name)).update(as_json)
        db.commit()

    @with_connection
    def is_ready(self, obj, db):
        try:
            db.query(obj.Meta.orm_model).count()
            return True
        except SQLAlchemyError:
            return False


database = Database()
