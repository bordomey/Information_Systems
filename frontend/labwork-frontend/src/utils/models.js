
export const Difficulty = {
  EASY: 'EASY',
  VERY_HARD: 'VERY_HARD',
  INSANE: 'INSANE',
  TERRIBLE: 'TERRIBLE'
};

export const Color = {
  GREEN: 'GREEN',
  BLACK: 'BLACK',
  YELLOW: 'YELLOW'
};


export class Coordinates {
  constructor(x = 0, y = 0) {
    this.x = Number(x) || 0;
    this.y = Number(y) || 0;
  }
}

export class Location {
  constructor(x = 0, y = 0, z = 0) {
    this.x = Number(x) || 0;
    this.y = Number(y) || 0;
    this.z = Number(z) || 0;
  }
}

export class Discipline {
  constructor(name = '', lectureHours = 0) {
    this.name = name;
    this.lectureHours = Number(lectureHours) || 0;
  }
}

export class Person {
  constructor(name = '', eyeColor = Color.GREEN, hairColor = Color.BLACK, location = null, height = 0, passportID = '') {
    this.name = name;
    this.eyeColor = eyeColor;
    this.hairColor = hairColor;
    this.location = location || new Location();
    this.height = Number(height) || 0;
    this.passportID = passportID;
  }
}

export class LabWork {
  constructor(
    id = null,
    name = '',
    coordinates = null,
    creationDate = new Date().toISOString(),
    description = '',
    difficulty = null,
    discipline = null,
    minimalPoint = 1,
    author = null
  ) {
    this.id = id;
    this.name = name;
    this.coordinates = coordinates || new Coordinates();
    this.creationDate = creationDate;
    this.description = description;
    this.difficulty = difficulty;
    this.discipline = discipline || new Discipline();
    this.minimalPoint = Number(minimalPoint) || 0;
    this.author = author;
  }
}