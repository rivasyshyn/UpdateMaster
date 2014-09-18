UpdateMaster
============

Android project. tool to rollup updates for apps


simple json config example:


{
    "description": "ipsum loreine",
    "name": "Repo name(optional at root node)",
    "resources": [
        {
            "description": "application description",
            "name": "application name",
            "resources": [
                {
                    "description": "package description",
                    "name": "package name",
                    "type": "PACKAGE",
                    "url": "url to package (*.apk)"
                },
                ...
            ],
            "type": "APPLICATION"
        },
        {
            "description": "package description",
            "name": "package name",
            "type": "PACKAGE",
            "url": "url to package (*.apk)"
        },
        ...
    ],
    "type": "REPOSITORY"
}
