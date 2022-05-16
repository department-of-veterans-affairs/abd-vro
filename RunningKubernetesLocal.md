# Running Kubernetes Locally

This is a step-by-step guide detailing how to run and test kubernetes locally.
This entire guide assumes the user is running on Mac OSX.

## Local Cluster (minikube)
`% brew install minikube` to install a kubernetes cluster tool. 

Use the listed commands to do the following:
- `% minikube start` - starts up a kubernetes cluster locally, creating a `default` namespace

# Kubernetes CLI (kubectl)
`% brew install kubectl` to install a kubernetes CLI tool.

Use the listed commands to do the following:
- `% kubens` - List all namespaces.
- `% kubens <namespace>` - Set the namespace to <namespace>.
- `% kubectl get ns` - List all namespaces in ps output format.
- `% kubectl get pods` - List all pods in ps output format, within the set namespace.
- `% kubectl get all` - List all pods, services, deployments, replicas in ps output format, within the set namespace.
- `% kubectl describe <type> <name>` - Print a detailed description of the selected resources, including related resources such as events or controllers. 
You may select a single object by name, all objects of that type, provide a name prefix, or label selector.
- `% kubectl logs <pod | type/name> -c <container>` - Print the logs for a container in a pod or specified resource. 
If the pod has only one container, the container name is optional.
- `% kubectl port-forward <type>/<name> <host-port>:<pod-port>` - Listen on `<host-port>` locally, forwarding to `<pod-port>` in the pod for `<type>/<name>`. 

## Kube Config

Locate the file `~/.kube/config`.

This can provide you information on whether your kubectl is pointing to a local Kubernetes cluster or one in the Cloud. 

For local testing, make sure its pointing to a local cluster!

## Istio

Istio is a [service mesh](https://istio.io/latest/docs/concepts/what-is-istio/) that provides a number of utilities for interacting with the Kubernetes cluster. 
`abd_vro` uses Istio in its Kubernetes config, so we will need to set it up on our local cluster as well.

`% brew install istioctl` to install Istio CLI tool.

Run `% install --set profile=demo` to install the Istio `demo` profile onto the cluster. 

Verify by running `% kubens` and seeing that the `istio-system` is now present

## Helm Templating

Helm is used as a templating tool for templating Kubernetes configuration.
`% brew install helm` to install the helm CLI tool.

Use the listed commands to do the following:

- `% helm template --output-dir=output/ helm` - Generate the Kubernetes config from the helm template directory in `abd_vro`
- `% helm upgrade --install abd_vro helm --set tag=latest -f helm/api-2-dev.yaml -n api-2-dev` - Run the latest docker image build and pushed to registry within local Kubernetes cluster.

