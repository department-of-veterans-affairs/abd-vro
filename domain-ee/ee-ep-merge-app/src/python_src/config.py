import os
from enum import Enum
from urllib.parse import quote, urlparse

config = {
    'app_id': os.environ.get('APP_ID') or 'EP_MERGE',
    'request_message_ttl': int(os.getenv('REQUEST_TTL') or 0),
    'request_retries': int(os.getenv('REQUEST_RETRIES') or 3),
    'response_max_latency': int(os.getenv('RESPONSE_TIMEOUT') or 30),
    'response_delivery_attempts': int(os.getenv('RESPONSE_DELIVERY_ATTEMPTS') or 3),
}


BIP_EXCHANGE = os.environ.get('BIP_API_EXCHANGE') or 'bipApiExchange'
BGS_EXCHANGE = os.environ.get('BGS_API_EXCHANGE') or 'svc_bgs_api.requests'


class ClientName(str, Enum):
    GET_CLAIM = 'getClaimClient'
    GET_CLAIM_CONTENTIONS = 'getClaimContentionsClient'
    GET_SPECIAL_ISSUE_TYPES = 'getSpecialIssueTypesClient'
    PUT_TSOJ = 'putTemporaryStationOfJurisdictionClient'
    CREATE_CLAIM_CONTENTIONS = 'createClaimContentionsClient'
    UPDATE_CLAIM_CONTENTIONS = 'updateClaimContentionsClient'
    CANCEL_CLAIM = 'cancelClaimClient'
    BGS_ADD_CLAIM_NOTE = 'addClaimNoteClient'


class ClientQueue:
    def __init__(self, exchange: str, request_queue: str, response_queue: str):
        self.exchange = exchange
        self.request_queue = request_queue
        self.response_queue = response_queue


CLIENTS: dict[ClientName, ClientQueue] = {
    ClientName.GET_CLAIM: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('GET_CLAIM_DETAILS_REQUEST') or 'getClaimDetailsQueue',
        os.environ.get('GET_CLAIM_DETAILS_RESPONSE') or 'getClaimDetailsResponseQueue',
    ),
    ClientName.GET_CLAIM_CONTENTIONS: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('GET_CLAIM_CONTENTIONS_REQUEST') or 'getClaimContentionsQueue',
        os.environ.get('GET_CLAIM_CONTENTIONS_RESPONSE') or 'getClaimContentionsResponseQueue',
    ),
    ClientName.GET_SPECIAL_ISSUE_TYPES: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('GET_SPECIAL_ISSUE_TYPES_REQUEST') or 'getSpecialIssueTypesQueue',
        os.environ.get('GET_SPECIAL_ISSUE_TYPES_RESPONSE') or 'getSpecialIssueTypesResponseQueue',
    ),
    ClientName.PUT_TSOJ: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('PUT_TSOJ_REQUEST') or 'putTempStationOfJurisdictionQueue',
        os.environ.get('PUT_TSOJ_RESPONSE') or 'putTempStationOfJurisdictionResponseQueue',
    ),
    ClientName.CREATE_CLAIM_CONTENTIONS: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('CREATE_CLAIM_CONTENTIONS_REQUEST') or 'createClaimContentionsQueue',
        os.environ.get('CREATE_CLAIM_CONTENTIONS_RESPONSE') or 'createClaimContentionsResponseQueue',
    ),
    ClientName.UPDATE_CLAIM_CONTENTIONS: ClientQueue(
        BIP_EXCHANGE,
        os.environ.get('UPDATE_CLAIM_CONTENTIONS_REQUEST') or 'updateClaimContentionsQueue',
        os.environ.get('UPDATE_CLAIM_CONTENTIONS_RESPONSE') or 'updateClaimContentionsResponseQueue',
    ),
    ClientName.CANCEL_CLAIM: ClientQueue(
        BIP_EXCHANGE, os.environ.get('CANCEL_CLAIM_REQUEST') or 'cancelClaimQueue', os.environ.get('CANCEL_CLAIM_RESPONSE') or 'cancelClaimResponseQueue'
    ),
    ClientName.BGS_ADD_CLAIM_NOTE: ClientQueue(
        BGS_EXCHANGE,
        os.environ.get('ADD_CLAIM_NOTE_REQUEST') or 'svc_bgs_api.add_note',
        os.environ.get('ADD_CLAIM_NOTE_RESPONSE') or 'ep_merge.add_note_response',
    ),
}


EP_MERGE_SPECIAL_ISSUE_CODE = os.environ.get('EP_MERGE_SPECIAL_ISSUE_CODE') or 'EMP'


def create_sqlalchemy_db_uri() -> str:
    user = quote(os.environ.get('POSTGRES_USER') or 'vro_user')
    password = quote(os.environ.get('POSTGRES_PASSWORD') or 'vro_user_pw')
    host = os.environ.get('POSTGRES_HOST') or 'localhost'
    port = os.environ.get('POSTGRES_PORT') or '5432'
    database = os.environ.get('POSTGRES_DB') or 'vro'
    postgres_url = os.environ.get('POSTGRES_URL')

    if postgres_url is None:
        return f'postgresql://{user}:{password}@{host}:{port}/{database}'

    result = urlparse(postgres_url)
    if not result.username:
        return result._replace(netloc=f'{user}:{password}@{result.netloc}').geturl()

    return postgres_url


ENV = os.environ.get('ENV') or 'local'
POSTGRES_SCHEMA = os.environ.get('POSTGRES_SCHEMA') or 'claims'
SQLALCHEMY_DATABASE_URI = create_sqlalchemy_db_uri()
