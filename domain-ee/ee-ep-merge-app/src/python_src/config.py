import os
from enum import Enum


def int_from_env(key, default):
    val = os.environ.get(key)
    if val is not None:
        return int(val)
    else:
        return default


config = {
    "app_id": os.environ.get("APP_ID") or "EP_MERGE",
    "request_timeout": int_from_env("REQUEST_TIMEOUT", 30),
    "request_retries": int_from_env("REQUEST_RETRIES", 3),
    "response_delivery_attempts": int_from_env("RESPONSE_DELIVERY_ATTEMPTS", 3)
}


class ClientName(str, Enum):
    GET_CLAIM = "getClaimClient"
    GET_CLAIM_CONTENTIONS = "getClaimContentionsClient"
    PUT_TSOJ = "putTemporaryStationOfJurisdictionClient"
    CREATE_CLAIM_CONTENTIONS = "createClaimContentionsClient"
    UPDATE_CLAIM_CONTENTIONS = "updateClaimContentionsClient"
    CANCEL_CLAIM = "cancelClaimClient"
    BGS_ADD_CLAIM_NOTE = "addClaimNoteClient"


BIP_EXCHANGE = "bipApiExchange"
BGS_EXCHANGE = "bgs-api"

EXCHANGES = {
    ClientName.GET_CLAIM: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.GET_CLAIM_CONTENTIONS: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.PUT_TSOJ: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.CREATE_CLAIM_CONTENTIONS: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.UPDATE_CLAIM_CONTENTIONS: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.CANCEL_CLAIM: os.environ.get("BIP_API_EXCHANGE") or BIP_EXCHANGE,
    ClientName.BGS_ADD_CLAIM_NOTE: os.environ.get("BGS_API_EXCHANGE") or BGS_EXCHANGE,
}

QUEUES = {
    ClientName.GET_CLAIM:
        os.environ.get("GET_CLAIM_DETAILS_REQUEST") or "getClaimDetailsQueue",
    ClientName.GET_CLAIM_CONTENTIONS:
        os.environ.get("GET_CLAIM_CONTENTIONS_REQUEST") or "getClaimContentionsQueue",
    ClientName.PUT_TSOJ:
        os.environ.get("PUT_TSOJ_REQUEST") or "putTempStationOfJurisdictionQueue",
    ClientName.CREATE_CLAIM_CONTENTIONS:
        os.environ.get("CREATE_CLAIM_CONTENTIONS_REQUEST") or "createClaimContentionsQueue",
    ClientName.UPDATE_CLAIM_CONTENTIONS:
        os.environ.get("UPDATE_CLAIM_CONTENTIONS_REQUEST") or "updateClaimContentionsQueue",
    ClientName.CANCEL_CLAIM:
        os.environ.get("CANCEL_CLAIM_REQUEST") or "cancelClaimQueue",
    ClientName.BGS_ADD_CLAIM_NOTE:
        os.environ.get("ADD_CLAIM_NOTE_REQUEST") or "add-note",
}

REPLY_QUEUES = {
    ClientName.GET_CLAIM:
        os.environ.get("GET_CLAIM_DETAILS_RESPONSE") or "getClaimDetailsResponseQueue",
    ClientName.GET_CLAIM_CONTENTIONS:
        os.environ.get("GET_CLAIM_CONTENTIONS_RESPONSE") or "getClaimContentionsResponseQueue",
    ClientName.PUT_TSOJ:
        os.environ.get("PUT_TSOJ_RESPONSE") or "putTempStationOfJurisdictionResponseQueue",
    ClientName.CREATE_CLAIM_CONTENTIONS:
        os.environ.get("CREATE_CLAIM_CONTENTIONS_RESPONSE") or "createClaimContentionsResponseQueue",
    ClientName.UPDATE_CLAIM_CONTENTIONS:
        os.environ.get("UPDATE_CLAIM_CONTENTIONS_RESPONSE") or "updateClaimContentionsResponseQueue",
    ClientName.CANCEL_CLAIM:
        os.environ.get("CANCEL_CLAIM_RESPONSE") or "cancelClaimResponseQueue",
    ClientName.BGS_ADD_CLAIM_NOTE:
        os.environ.get("ADD_CLAIM_NOTE_RESPONSE") or "add-note-response",
}
