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


class ClientName(str, Enum):
    GET_CLAIM = 'getClaimClient'
    GET_CLAIM_CONTENTIONS = 'getClaimContentionsClient'
    PUT_TSOJ = 'putTemporaryStationOfJurisdictionClient'
    CREATE_CLAIM_CONTENTIONS = 'createClaimContentionsClient'
    UPDATE_CLAIM_CONTENTIONS = 'updateClaimContentionsClient'
    CANCEL_CLAIM = 'cancelClaimClient'
    BGS_ADD_CLAIM_NOTE = 'addClaimNoteClient'
    BIP_DEAD_LETTER = 'bipDeadLetterQueue'


BIP_EXCHANGE = 'bipApiExchange'
BGS_EXCHANGE = 'bgs-api'
DLQ_EXCHANGE = 'bipApi.dlx'

EXCHANGES = {
    ClientName.GET_CLAIM: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.GET_CLAIM_CONTENTIONS: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.PUT_TSOJ: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.CREATE_CLAIM_CONTENTIONS: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.UPDATE_CLAIM_CONTENTIONS: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.CANCEL_CLAIM: os.environ.get('BIP_API_EXCHANGE') or BIP_EXCHANGE,
    ClientName.BGS_ADD_CLAIM_NOTE: os.environ.get('BGS_API_EXCHANGE') or BGS_EXCHANGE,
    ClientName.BIP_DEAD_LETTER: os.environ.get('BIP_API_DLQ_EXCHANGE') or DLQ_EXCHANGE,
}

QUEUES = {
    ClientName.GET_CLAIM: os.environ.get('GET_CLAIM_DETAILS_REQUEST') or 'getClaimDetailsQueue',
    ClientName.GET_CLAIM_CONTENTIONS: os.environ.get('GET_CLAIM_CONTENTIONS_REQUEST') or 'getClaimContentionsQueue',
    ClientName.PUT_TSOJ: os.environ.get('PUT_TSOJ_REQUEST') or 'putTempStationOfJurisdictionQueue',
    ClientName.CREATE_CLAIM_CONTENTIONS: os.environ.get('CREATE_CLAIM_CONTENTIONS_REQUEST') or 'createClaimContentionsQueue',
    ClientName.UPDATE_CLAIM_CONTENTIONS: os.environ.get('UPDATE_CLAIM_CONTENTIONS_REQUEST') or 'updateClaimContentionsQueue',
    ClientName.CANCEL_CLAIM: os.environ.get('CANCEL_CLAIM_REQUEST') or 'cancelClaimQueue',
    ClientName.BGS_ADD_CLAIM_NOTE: os.environ.get('ADD_CLAIM_NOTE_REQUEST') or 'add-note',
}

REPLY_QUEUES = {
    ClientName.GET_CLAIM: os.environ.get('GET_CLAIM_DETAILS_RESPONSE') or 'getClaimDetailsResponseQueue',
    ClientName.GET_CLAIM_CONTENTIONS: os.environ.get('GET_CLAIM_CONTENTIONS_RESPONSE') or 'getClaimContentionsResponseQueue',
    ClientName.PUT_TSOJ: os.environ.get('PUT_TSOJ_RESPONSE') or 'putTempStationOfJurisdictionResponseQueue',
    ClientName.CREATE_CLAIM_CONTENTIONS: os.environ.get('CREATE_CLAIM_CONTENTIONS_RESPONSE') or 'createClaimContentionsResponseQueue',
    ClientName.UPDATE_CLAIM_CONTENTIONS: os.environ.get('UPDATE_CLAIM_CONTENTIONS_RESPONSE') or 'updateClaimContentionsResponseQueue',
    ClientName.CANCEL_CLAIM: os.environ.get('CANCEL_CLAIM_RESPONSE') or 'cancelClaimResponseQueue',
    ClientName.BGS_ADD_CLAIM_NOTE: os.environ.get('ADD_CLAIM_NOTE_RESPONSE') or 'add-note-response',
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
