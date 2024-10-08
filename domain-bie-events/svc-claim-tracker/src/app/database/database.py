import logging
from typing import Generic, Type, TypeVar

from sqlalchemy.exc import SQLAlchemyError
from sqlmodel import Session, SQLModel, select

ModelTypeT = TypeVar('ModelTypeT', bound=SQLModel)


class Database(Generic[ModelTypeT]):
    def __init__(self, model: Type[ModelTypeT]):
        self.model = model

    def add(self, db: Session, obj: ModelTypeT) -> ModelTypeT:
        db.add(obj)
        db.commit()
        db.refresh(obj)
        return obj

    def is_ready(self, db: Session) -> bool:
        try:
            db.exec(select(self.model).limit(1))
            return True
        except SQLAlchemyError as e:
            logging.error(e)
            return False
