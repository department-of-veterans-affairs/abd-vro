from fastapi.testclient import TestClient


def test_claim_ids_logged(client: TestClient, caplog):
    json_post_dict = {
        "va_gov_claim_id": 100,
        "vbms_claim_id": 200,
    }
    client.post("/claim-linker", json=json_post_dict)
    expected_claim_link_json = {
        "message": "linking claims",
        "va_gov_claim_id": 100,
        "vbms_claim_id": 200,
    }
    assert eval(caplog.records[0].message) == expected_claim_link_json
