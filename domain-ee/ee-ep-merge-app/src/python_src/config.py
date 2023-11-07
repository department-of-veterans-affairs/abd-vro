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
    UPDATE_CLAIM_CONTENTIONS = "updateClaimContentionsClient"
    CANCEL_CLAIM = "cancelClaimClient"


EXCHANGE = "bipApiExchange"

QUEUES = {
    ClientName.GET_CLAIM:
        os.environ.get("GET_CLAIM_DETAILS_REQUEST") or "getClaimDetailsQueue",
    ClientName.GET_CLAIM_CONTENTIONS:
        os.environ.get("GET_CLAIM_CONTENTIONS_REQUEST") or "getClaimContentionsQueue",
    ClientName.PUT_TSOJ:
        os.environ.get("PUT_TSOJ_REQUEST") or "putTemporaryStationOfJurisdictionQueue",
    ClientName.UPDATE_CLAIM_CONTENTIONS:
        os.environ.get("UPDATE_CLAIM_CONTENTIONS_REQUEST") or "updateClaimContentionQueue",
    ClientName.CANCEL_CLAIM:
        os.environ.get("CANCEL_CLAIM_REQUEST") or "cancelClaimQueue",
}

REPLY_QUEUES = {
    ClientName.GET_CLAIM:
        os.environ.get("GET_CLAIM_DETAILS_RESPONSE") or "getClaimDetailsResponseQueue",
    ClientName.GET_CLAIM_CONTENTIONS:
        os.environ.get("GET_CLAIM_CONTENTIONS_RESPONSE") or "getClaimContentionsResponseQueue",
    ClientName.PUT_TSOJ:
        os.environ.get("PUT_TSOJ_RESPONSE") or "putTemporaryStationOfJurisdictionResponseQueue",
    ClientName.UPDATE_CLAIM_CONTENTIONS:
        os.environ.get("UPDATE_CLAIM_CONTENTIONS_RESPONSE") or "updateClaimContentionResponseQueue",
    ClientName.CANCEL_CLAIM:
        os.environ.get("CANCEL_CLAIM_RESPONSE") or "cancelClaimResponseQueue",
}
