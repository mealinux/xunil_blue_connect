class UUIDS {
  final String? name;
  final String? shortDescription;
  final String? uuid;

  UUIDS({
    this.name,
    this.shortDescription,
    this.uuid,
  });

  factory UUIDS.fromJson(Map<String, dynamic> json) => UUIDS(
        name: json["name"].toString(),
        shortDescription: json["short_description"].toString(),
        uuid: json["uuid"].toString(),
      );
}
