package dev.dumbanov.fociumapi.service;

import com.cloudinary.Cloudinary;
import dev.dumbanov.fociumapi.entity.Profile;
import dev.dumbanov.fociumapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    private final Cloudinary cloudinary;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return profileRepository.findProfileByEmail(username);
    }

    public Profile getProfileByEmail(String email) {
        if(!profileRepository.existsProfileByEmail(email))
            throw new UsernameNotFoundException("Profile with email " + email + " not found");
        return profileRepository.findProfileByEmail(email);
    }

    public Profile uploadPfp(MultipartFile file, String email) throws IOException {
        var profile = getProfileByEmail(email);

        if(file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        var uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                "folder", "focium/pfps",
                "public_id", "pfp_" + profile.getUuid(),
                "overwrite", true
        ));
        var secureUrl = uploadResult.get("secure_url").toString();
        profile.setPfp(secureUrl);
        return profileRepository.save(profile);
    }
}
