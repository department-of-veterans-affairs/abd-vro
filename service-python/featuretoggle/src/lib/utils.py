import yaml


def create_features_list():
    """
    Parse a yaml file into a usable feature flags with boolean values

    :return: feature flag list
    :rtype: list
    """

    stream = open('features.yml', 'r')
    data = yaml.load(stream)

    features = []

    for k, v in data['features'].items():
        temp = [k, data['features'][k]['enabled']]
        features.append(temp)

    return features
