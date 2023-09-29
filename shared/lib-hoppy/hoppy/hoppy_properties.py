from dataclasses import dataclass


@dataclass
class ExchangeProperties:
    name: str = '',
    type: str = 'direct'
    passive_declare: bool = True
    durable: bool = True
    auto_delete: bool = False


@dataclass
class QueueProperties:
    name: str = '',
    passive_declare: bool = True
    durable: bool = True
    auto_delete: bool = False
    exclusive: bool = False
