package com.example.digitalwallet.service;


import com.example.digitalwallet.dto.RegisterRequest;
import com.example.digitalwallet.model.Customer;
import com.example.digitalwallet.model.User;
import com.example.digitalwallet.repository.CustomerRepository;
import com.example.digitalwallet.repository.UserRepository;
import com.example.digitalwallet.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class) // mockitonun JUnit ile çalısmasını sağlar bu anatasyon
public class AuthServiceTest {
    // @Mock: Servisimizin bağımlı olduğu sınıfları (örneğin UserRepository) taklit etmek için kullanılır.
    //@InjectMock test edeceğimiz gerçek sınıfı ( AuthService) tanımlar ve @Mock ile
    // işaretlediğimiz taklitleri otomatik olarak bu servisin içine enjekte eder.

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;




    @Test
    public void successfullyRegister() {
        // 1. Arrange: Girdi verilerini ve mock davranışlarını hazırladık
        User user = new User();
        user.setUsername("abdullah_user");
        user.setPassword("sifre123");

        RegisterRequest request = new RegisterRequest();
        request.setName("Abdullah");
        request.setSurname("Test");
        request.setTckn("12345678901");
        request.setUser(user);

        String encodedPassword = "encoded_sifre_999";


        // Mockito stubs -> metotlar çağrıldığında ne döneceklerini belirliyoruz
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        // 2. Act: gerçek metodu çağırıyoruz
        String result = authService.register(request);

        // 3. Assert: sonuçları karşılaştırıyoruz
        assertNotNull(result);
        assertEquals("Kayıt başarılı! Lütfen e-postanızı kontrol ederek hesabınızı doğrulayın.", result);
        assertEquals(encodedPassword, user.getPassword()); // Şifrenin encode edildiğini kontrol ediyoruz
        assertEquals(User.Role.ROLE_CUSTOMER, user.getRole()); // Rolün doğru set edildiğini kontrol ediyoruz
        assertFalse(user.isVerified());
        assertNotNull(user.getVerificationToken());

        // 4. Verify: Bağımlılıkların (repository vb.) doğru şekilde çağrıldığından emin oluyoruz
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(emailService, times(1)).sendVerificationMail(eq(user.getUsername()), anyString());
    }

    @Test
    public void successfullyLogin() {
        // 1. Arrange: Gerekli verileri ve mock nesnelerini hazırladık
        String username = "abdullah_user";
        String password = "correct_password";
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.successful_login_token";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded_password");
        user.setVerified(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(fakeToken);

        // 2. Act: asıl metodu calıstırıoruz
        String result = authService.login(username, password, authenticationManager);

        // 3. Assert: Sonucu doğrula
        assertNotNull(result);
        assertEquals(fakeToken, result);

        // 4. Verify: İşlemlerin doğru sırayla yapıldığını denetledik
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(username);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    public void loginShouldFailWhenEmailNotVerifiedTest(){

        String username = "abdullah_user";
        String password = "correct_password";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded_password");
        user.setVerified(false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));


        RuntimeException runtimeException=assertThrows(RuntimeException.class,() -> authService.login(username,password,authenticationManager));
        assertEquals("Lütfen önce e-posta adresinizi doğrulayın.",runtimeException.getMessage());


        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtService, never()).generateToken(user);
    }

    @Test
    public void successfullyVerifyEmail(){
        String verificationToken="xyzvacasdweasd";
        User user =new User();
        user.setVerified(false);
        user.setVerificationToken(verificationToken);

        when(userRepository.findByVerificationToken(verificationToken)).thenReturn(Optional.of(user));

        String result=authService.verifyEmail(verificationToken);

        assertNotNull(result);
        assertEquals("Hesabınız başarıyla doğrulandı! Artık giriş yapabilirsiniz.",result);
        assertTrue(user.isVerified());
        assertNull(user.getVerificationToken());

        verify(userRepository,times(1)).findByVerificationToken(verificationToken);
        verify(userRepository,times(1)).save(user);

    }



}
