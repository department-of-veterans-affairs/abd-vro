#!/bin/bash

ENV=$(tr '[A-Z]' '[a-z]' <<< "$1")

#verify we have an environment set
if [ "${ENV}" != "sandbox" ] && [ "${ENV}" != "dev" ] && [ "${ENV}" != "qa" ] && [ "${ENV}" != "prod" ]
then
  echo "Please enter valid environment (dev, sandbox, qa, prod)" && exit 1
fi

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: abd-vro
  namespace: va-abd-rrd-${ENV}
data:
  lhAccessClientId: MG9haDI4cmI1akxqNE5lU04ycDc=
  lhAssertionUrl: aHR0cHM6Ly9kZXB0dmEtZXZhbC5va3RhLmNvbS9vYXV0aDIvYXVzOG5tMXEwZjdWUTBhNDgycDcvdjEvdG9rZW4=
  lhFhirUrl: aHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3Yvc2VydmljZXMvZmhpci92MC9yNA==
  lhPrivateKey: LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb3dJQkFBS0NBUUVBdlRSQTg4WFVsUmc2eXFDdVQzUm1yRjg0RmwzaXd3TlpEaVVFL0tnN2dQL0ZNUVNmClFkMWtRZEQ4d2I0TjhoQ0FZcEFDdTRDdHRjLy90NVBLL21Ia0JCOHlaT0dzbEg3ZC9nTmVMeVNjTzhRb0xPQXoKUDZ1NGNnT3l0aGNDUEMrMGErQk51c1dNbXRrUmJyekN4QzJpZU93b28zS3Z0WUNkY1g2SDNQTjZxcDlyZ2NabgpBT29tS2JWTm9RMENUWWJyNWJkYjVuYjFZTVhpZGJPZmltY3VReEVwQVp2bjVPMDVQVmdFQWs2YjBkbW8rZmRGCkZYTHJuSno5QUJWOEgwMXB0Ym9rWVNIWmZlc1Y1QW1sa0RxbmRseUxmbjZjdWRIVUdVbU82empEc2s1R3JQcksKUWIzeUg4Y1V3TWNXZmRXelZyVFl3M3NxRktNdWVtR3lOVEkzSHdJREFRQUJBb0lCQUdqRUEyYzRWd01MdkpvdwpxUFNFRHNwSm0yUC9McTR2TDJPK3FlbEJPbG11eHNEaXYxQ1lSNGlPTVRjU2h1UlBzTmRpeUxDTWl5T09maGxuCnM5SngzL0ZNV1BvMVgvdUYyR1JyOXY0QnRxVDlkQVdOUzdObzRhUDRXaitycytnaHZqU3hxampPWkpIbHVscG8KR1N6SjNCbzdJakdITXJZODluRSt6L3BQczgrL3JPZi8xRXErVk9qTklpczR4RkxnT3BPekVmL3kwbFJYRTUyYQpqL1Z5OFBXSlFXcHJFLzBScWRKM2ZZOTBEL2ZjcnVNUDlPK2gxc1JDa2ZIOEJJaGs1NksvL0JQQW5PR2F3OTJhCnpyY2xzU1hleVFncjIzalhjZmZxRk1mLzdiRTY4bTN3V2FtSkN3aHBqbHBDRHlXOXhOSTAzV1FaY1RGNGNRZE8KMWRQNENwa0NnWUVBM29TYlVKTTd1MGd1MWdwbFovSDdtZ3RESFp5MjYrdEZ0VkZVRC9aWG5ZblVHZ0VaRmZ2eApoRUNZY0FmQ0pIY2ZZbzhoQzRiNFFMYVJ0VUlvTFRZdmNVWldVSTBZOG5DZXd1QXpsR3B5eFAvRXVlNjVWeGI3CmNHTVdOV0tuTTZkWFo3UUMrUVJpSWcxS1k4NlBOQnVJTGYyc2JObFcrNzhuV1prVkhsYjYwT3NDZ1lFQTJheGwKZWJrbkhYTTJ6UllXVngrMkZuMkt3WnRwLzZHR1d6c3pTSERmcndhNU1JYjFkMnZXMTA1eHJxcUw1K3hiRUFJSApqb3QySVBSaUtXa3BIcGxBRHJjeGNFTmd1Q05jS0lRbE4wcWR6WFZHT2lpZTYxa1J3UmlzZUdIVDA5cytTdjNVCmd6SmdmR09HVitmbENtdHBKam14S3VMOVFKZHVleUN4RDRjQ2haMENnWUFnZUNNZHQ4NUFmc0R1MUtlRmFtYjMKVEd0bnQ4VnRXWEJiaTltRFZzUGtKZ2VBSCtia01WWkZvUk5kVFpTRHM2b3R2SXJxM3EvODZXY0xsdks3M2l0QQpFUzVwSkoyZVN0YXZ3djFqeThDaGhGelZjTU0wdFJMTmpIZmlKRm9YakhTUm1rL1F2RllCZUFVQjJWTkdIRHBiCjA2ZEVYdFoxZDZnclI4NHdGaXNWTHdLQmdRQ0hEcTlicWlUNTRkMnp2Q3VhWFNPbXFyaG9UaHlqcnIzbFowTDMKaElsVldNK1lxM1FzWjYzQ28zcG56SXZJRjkrY2ZCWGlQT2ZMcExZdlNYOW82OTlTQWhwYnRJS1RYZVUxZ21CeApBUXdPU2VDTC8xNHpXbTFvcVAyQ0ovaTlyMy9rR2NBOHN3b293N3U4V1RiRWswYUhvQzRzRlBEYm5kbS83ZFhPCmM0OStCUUtCZ0c1eHZEQmhTb2UrTUdNVjlOeXVUa2RkbENuWWFUUzBuRUh1d2QwdFAxMmduM3dOZFdxcGp2Y28KUDlvK09BMUdqT1JhN2h0elRWQ2J0RkpZVCtOQXFRSWhEdm5ZTzl1Z2x2b0ZFU01qMytWTjM1V0ZlNTN4WXFzNwpXbnduUHJvbHUxR255VUsyS3k3Z1gyMWRrdWtkNFNYdmhyMmZXV2ppZTZWVlo5ZWUzSmZFCi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tCgo=
  lhTokenUrl: aHR0cHM6Ly9zYW5kYm94LWFwaS52YS5nb3Yvb2F1dGgyL2hlYWx0aC9zeXN0ZW0vdjEvdG9rZW4=
EOF

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: api
  namespace: va-abd-rrd-${ENV}
data:
  apiKey01: NmYwMTg4ZWUtMzk5MS00ZjBjLTllZjAtYjk0NmZkNmM0MDQ1
  apiKey02: NmVkN2U3YTQtM2YzMi00NGQyLTg5MTAtNmQwMDhhMTRjMjk2
EOF

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: dbinit
  namespace: va-abd-rrd-${ENV}
data:
  flyway.baselineOnMigrate: ZmFsc2U=
  flyway.cleanDisabled: ZmFsc2U=
  flyway.cleanOnValidationError: dHJ1ZQ==
  flyway.connectRetries: MTA=
  flyway.createSchemas: dHJ1ZQ==
  flyway.defaultSchema: Y2xhaW1z
  flyway.encoding: SVNPLTg4NTktMQ==
  flyway.group: ZmFsc2U=
  flyway.installedBy: bXktdXNlcg==
  flyway.locations: Y2xhc3NwYXRoOmdvdi52YS5zdGFydGVyLmV4YW1wbGUubWlncmF0aW9uLGRhdGFiYXNlL21pZ3JhdGlvbnMsZmlsZXN5c3RlbTovZmx5d2F5L3NxbC9taWdyYXRpb25z
  flyway.mixed: dHJ1ZQ==
  flyway.outOfOrder: ZmFsc2U=
  flyway.password: bm90LXRoZS1wYXNzd29yZA==
  flyway.placeholderPrefix: I1s=
  flyway.placeholderReplacement: dHJ1ZQ==
  flyway.placeholderSuffix: XQ==
  flyway.placeholders.adminname: ZXhhbXBsZV9hZG1pbg==
  flyway.placeholders.adminpassword: YWxzby1ub3QtdGhlLWFkbWluLXBhc3N3b3Jk
  flyway.placeholders.dbname: dnJv
  flyway.placeholders.schemaname: Y2xhaW1z
  flyway.placeholders.servicename: ZXhhbXBsZV9zZXJ2aWNl
  flyway.placeholders.servicepassword: YWxzby1ub3QtdGhlLXNlcnZpY2UtcGFzc3dvcmQ=
  flyway.placeholders.username: ZXhhbXBsZV91c2Vy
  flyway.placeholders.userpassword: YWxzby1ub3QtdGhlLXVzZXItcGFzc3dvcmQ=
  flyway.repeatableSqlMigrationPrefix: UlJS
  flyway.schemas: Y2xhaW1z
  flyway.skipDefaultCallbacks: ZmFsc2U=
  flyway.sqlMigrationPrefix: Vg==
  flyway.sqlMigrationSeparator: X18=
  flyway.sqlMigrationSuffixes: LnNxbA==
  flyway.table: c2NoZW1hX2hpc3Rvcnk=
  flyway.tablespace: cGdfZGVmYXVsdA==
  flyway.target: bGF0ZXN0
  flyway.url: amRiYzpwb3N0Z3Jlc3FsOi8vdnJvLWFwaS1wb3N0Z3Jlcy1zZXJ2aWNlOjU0MzIvdnJvP3VzZXI9dnJvX3VzZXImcGFzc3dvcmQ9bm90LXRoZS1wYXNzd29yZA==
  flyway.user: dnJvX3VzZXI=
  flyway.validateOnMigrate: dHJ1ZQ==
EOF

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: postgres
  namespace: va-abd-rrd-${ENV}
data:
  dbName: dnJv
  initDbName: dnJv
  initPassword: bm90LXRoZS1wYXNzd29yZA==
  initUsername: dnJvX3VzZXI=
  password: bm90LXRoZS1wYXNzd29yZA==
  schema: Y2xhaW1z
  url: amRiYzpwb3N0Z3Jlc3FsOi8vdnJvLWFwaS1wb3N0Z3Jlcy1zZXJ2aWNlOjU0MzIvdnJv
  username: dnJvX3VzZXI=
EOF

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: rabbitmq
  namespace: va-abd-rrd-${ENV}
data:
  Pass: Z3Vlc3Q=
  User: Z3Vlc3Q=
EOF

cat << EOF | kubectl apply -f -
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  name: redis
  namespace: va-abd-rrd-${ENV}
data:
  redisHost: NjM3OQ==
  redisPassword: dGhlPVZlcnk9TDBuZz09PVJlZGlzPT1QYXNzd29yZA==
EOF