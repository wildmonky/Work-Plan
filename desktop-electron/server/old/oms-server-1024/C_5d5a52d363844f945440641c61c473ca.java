import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5d5a52d363844f945440641c61c473ca
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapYesNo.get(mapRow.get("ISPK"));

    }
}
