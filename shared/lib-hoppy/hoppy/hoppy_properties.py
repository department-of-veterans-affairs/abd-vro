from dataclasses import dataclass


@dataclass
class ExchangeProperties:
    name: str = '',
    type: str = 'direct'
    passive_declare: bool = True
    durable: bool = True
    auto_delete: bool = True


@dataclass
class QueueProperties:
    name: str = '',
    passive_declare: bool = True
    durable: bool = True
    auto_delete: bool = True
    exclusive: bool = False
