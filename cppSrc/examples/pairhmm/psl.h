#ifndef __PSL_H
#define __PSL_H

struct wed {
  //C TYPE:   NAME:         VHDL TYPE:                        Description
    __u8      status      ; //std_logic_vector(7 downto 0);
    __u8      wed00_a     ; //std_logic_vector(7 downto 0);
    __u16     wed00_b     ; //std_logic_vector(15 downto 0);
    __u32     wed00_c     ; //std_logic_vector(31 downto 0);

    __u64     *source     ; //unsigned(63 downto 0);          Source Address of batches
    __u64     *destination; //unsigned(63 downto 0);          Destination Addres of result

    __u32     batch_size  ; //unsigned(31 downto 0);          Size of a batch in bytes
    __u32     pair_size   ; //unsigned(31 downto 0);          Size of a pair

    __u32     padded_size ; //unsigned(31 downto 0);
    __u32     batches     ; //unsigned(31 downto 0);

    __u64     wed05       ; //std_logic_vector(63 downto 0);
    __u64     wed06       ; //std_logic_vector(63 downto 0);
    __u64     wed07       ; //std_logic_vector(63 downto 0);
    __u64     wed08       ; //std_logic_vector(63 downto 0);
    __u64     wed09       ; //std_logic_vector(63 downto 0);
    __u64     wed10       ; //std_logic_vector(63 downto 0);
    __u64     wed11       ; //std_logic_vector(63 downto 0);
    __u64     wed12       ; //std_logic_vector(63 downto 0);
    __u64     wed13       ; //std_logic_vector(63 downto 0);
    __u64     wed14       ; //std_logic_vector(63 downto 0);
    __u64     wed15       ; //std_logic_vector(63 downto 0);
};

#endif
