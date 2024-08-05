from dataclasses import dataclass
from dataclasses import field

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
    arguments: dict = field(default_factory=dict)
